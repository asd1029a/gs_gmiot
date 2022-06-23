package com.danusys.web.commons.api.scheduler.socket;

import com.danusys.web.commons.api.dto.CctvDTO;
import com.danusys.web.commons.api.model.Facility;
import com.danusys.web.commons.api.scheduler.service.FacilityTcpService;
import com.danusys.web.commons.api.util.XmlDataUtil;
import com.danusys.web.commons.app.IOUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/06/16
 * Time : 5:56 PM
 */
@Slf4j
@Component
@Profile(value = {"bsng", "gm"})
public class FacilityTcpServer {
    private int threadID = 0;
    private ServerSocket server;
    private int port = 8040;
    private static boolean isClosed = false;
    private static String dest = "";
    private Map<String, String> vmsServerData;
    private FacilityTcpService facilityTcpService;

    class ServiceProcessor extends Thread {
        private String dest_;
        private Socket connectedSocket;
//		private int serviceid;

        public ServiceProcessor(Socket connectedSocket, String dest) {
            this.connectedSocket = connectedSocket;
//			this.serviceid = serviceid;
//			this.agentController_ = agentController;
            this.dest_ = dest;
        }

        @SneakyThrows
        public void run() {
            try {
                log.info("#########################");
                BufferedOutputStream bos = new BufferedOutputStream(this.connectedSocket.getOutputStream());
                InputStream is = this.connectedSocket.getInputStream();
//				BufferedInputStream bis = new BufferedInputStream(is);
// 				ByteArrayOutputStream bbuffer = new ByteArrayOutputStream();

//				int ba;
                byte[] bc = IOUtils.toByteArray(is);

                String[] hexCode = byteArrayToHex(bc).split(" ");
//				String s2 = new String(bc, "UTF-8");
                String hexStrLen = hexCode[2] + hexCode[3] + hexCode[4] + hexCode[5];
                int dataLen = Integer.parseInt(hexStrLen, 16);

                log.info("dataLen = {}", dataLen);

                byte[] copy = null;
                String subData = "";

                if ("42".equals(hexCode[1])) {
                    int byteLength = bc.length;
                    int rainLength = byteLength - 6;
                    copy = new byte[rainLength];
                    System.arraycopy(bc, 6, copy, 0, rainLength);
                    subData = new String(copy, "UTF-8");
                } else {
                    copy = new byte[dataLen];
                    System.arraycopy(bc, 6, copy, 0, dataLen);
                    subData = new String(copy, "UTF-8");
                }


//				String socketAddress = new InetSocketAddress(this.connectedSocket.getInetAddress(), this.connectedSocket.getPort()).toString();
//				String remoteIp = socketAddress.substring(1).split(":")[0];

                String rMsg = "SUCCESS";

                if ("12".equals(hexCode[1]) && "02".equals(hexCode[0]) && "03".equals(hexCode[hexCode.length - 1])) {
                    log.info(" ===== data : {}", "NodeStatusRsp");
                    int idx = subData.indexOf("</ngvms:envelope>");
                    int start = subData.indexOf("<?xml");
                    subData = subData.substring(start, idx + 17);
//					this.agentController_.getNodeStatusRsp(subData);
                } else if ("11".equals(hexCode[1]) && "02".equals(hexCode[0])
                        && "03".equals(hexCode[hexCode.length - 1])) {
                    log.info(" ===== data : {}", "AllCenterList");

                    log.info("---------------------------- AllCenterList Start ----------------------------");

//                    log.info("subData {} ", subData);

//					this.agentController_.allCenterList(subData);
                    final CctvDTO cctvDTO = XmlDataUtil.getCctvInfo(subData);
                    String vmsSvrIp = cctvDTO.getGetAllCenterListRsp().getVMS_SVR_IP();
                    String vmsSvrNo = vmsServerData.get(vmsSvrIp);

                    cctvDTO.getGetAllCenterListRsp().getStreamNodeList().getStreamNodeInfo().stream().forEach(f -> {
                        CctvDTO.GetAllCenterListRsp.StreamNodeList.StreamNodeInfo.StreamNodeBaseInfo streamNodeBaseInfo = f.getBaseInfo();
                        CctvDTO.GetAllCenterListRsp.StreamNodeList.StreamNodeInfo.SourceInfo sourceInfo = f.getSourceInfo();
                        CctvDTO.GetAllCenterListRsp.StreamNodeList.StreamNodeInfo.SourceInfo.ExternalSrcInfo externalSrcInfo = sourceInfo.getExternalSrcInfo();
                        CctvDTO.GetAllCenterListRsp.StreamNodeList.StreamNodeInfo.SourceInfo.ExternalSrcInfo.WebService webService = externalSrcInfo.getWebService();
                        String modelName = externalSrcInfo.getModelName();
                        String sourceType = sourceInfo.getSourceType();
                        String nodeId = streamNodeBaseInfo.getID();
                        String facilityName = streamNodeBaseInfo.getName();
                        String ip = streamNodeBaseInfo.getIP();
                        String port = streamNodeBaseInfo.getPORT();
                        String connected = streamNodeBaseInfo.getConnected();
//                        String id = webService.getID();
//                        String password = webService.getPassword();
                        String id = "admin";
                        String password = "1234";
                        String rtspUrl = MessageFormat.format("rtsp://{0}:{1}@{2}:8554/site{3}/video", id, password, ip, nodeId);
                        String isPtz = f.getIsPtz();
                        String ptzType = f.getPtzType();
                        String cctvPurpose = f.getCctvPurpose();
                        String managementCode = streamNodeBaseInfo.getManagementCode();
                        double latitude = f.getLatitude().isEmpty() ? 0 : Double.parseDouble(f.getLatitude());
                        double longitude = (f.getLongitude().isEmpty() ? 0 : Double.parseDouble(f.getLongitude()));

                        Map<String, Object> optData = new HashMap<>();

                        optData.put("rtsp_url", rtspUrl);
                        optData.put("node_id", nodeId);
                        optData.put("is_ptz", isPtz);
                        optData.put("ptz_type", ptzType);
                        optData.put("cctv_purpose", cctvPurpose);
                        optData.put("management_code", managementCode);

                        Facility facility;
                        if (sourceType.equals("5")) {
                            String cameraCount = sourceInfo.getCameraCount();
                            int count = cameraCount.isEmpty() ? 0 : Integer.parseInt(cameraCount);
                            facilityTcpService.addGroupCctv(count, nodeId, facilityName, latitude, longitude, vmsSvrNo, optData);
                        } else if (modelName.equals("Guardian")) {
                            String cameraCount = sourceInfo.getExternalSrcInfo().getCameraCount();
                            int count = cameraCount.isEmpty() ? 0 : Integer.parseInt(cameraCount);
                            facilityTcpService.addGuardianCctv(count, nodeId, facilityName, latitude, longitude, vmsSvrNo, optData);
                        } else {
                            facilityTcpService.addCctv(nodeId, facilityName, latitude, longitude, vmsSvrNo, optData);
                        }
                        // String facilityId = nodeId + "_" +
                        // Facility facility = Facility.builder().facilityId()
                    });

                    log.info("---------------------------- AllCenterList End ----------------------------");


                }


                bos.write(rMsg.getBytes("utf-8"));
                bos.flush();
            } catch (EOFException ee) {
                log.error(" ===== EOFException : {}", ee.getMessage());
            } catch (IOException e) {
                log.error(" ==== IOException : {}", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // shutDownServer();
            }
        }
    }

//    public FacilityTcpServer() {
//        log.debug(" ===== FacilityTcpServer Begin ===== ");
//    }
//
//    public static void main(String[] args) throws IOException {
//        try {
//            FacilityTcpServer server = new FacilityTcpServer();
//            server.startServer();
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(" ===== TCPServerException : {}", e.getMessage());
//        }
//    }


    public FacilityTcpServer(@Value("#{${vms.server.map}}") Map<String, String> vmsServerData,
                             FacilityTcpService facilityTcpService) {
        this.vmsServerData = vmsServerData;
        this.facilityTcpService = facilityTcpService;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startServer();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error(" ===== TCPServerException : {}", e.getMessage());
                }
            }
        }).start();
    }

    public void startServer() throws Exception {

        InetAddress srvIP = InetAddress.getLocalHost();
        this.port = 8040; //ConfigManager.getIntProperty("tcp_server.port");
        this.server = new ServerSocket(this.port);
        //dest=EM1^DSCP^170^41460
        //EM1 = 127.0.0.1|5771|10000|10000|5|Y
        //SAS = 127.0.0.1|5775|10000|10000|5|Y
        String dest1 = "EM1^DSCP^170^41460";//ConfigManager.getProperty("dest").trim();
        String[] destArr = dest1.split("\\^");

        // logger.info(" ===== 이벤트 destArray >>>> {}", Integer.valueOf(destArr.length));
        int aCnt = destArr.length - 3;
        for (int i = 0; i < aCnt; i++) {
            String dest_code = destArr[i];
            dest = dest + dest_code + "|" + "127.0.0.1|5771|10000|10000|5|Y";//ConfigManager.getProperty(dest_code).trim();
            dest += "^";
        }
        dest = dest + destArr[(destArr.length - 3)] + "^" + destArr[(destArr.length - 2)] + "^"
                + destArr[(destArr.length - 1)];

        log.info(" ===== 시설물 수신 서버실행 >>>> SrvIP:[{}], SrvPort:[{}], destination:[{}]",
                new Object[] { srvIP.getHostAddress(), Integer.valueOf(this.port), dest });
        try {
            while (true) {
                Socket connectedSocket = this.server.accept();
                log.error(" ===== connected Socket threadID : {}", this.threadID);
                InetSocketAddress rtIP = (InetSocketAddress) connectedSocket.getRemoteSocketAddress();
                log.info(" ===== connected Socket Info >>>> CtIP:[{}], CtPort:[{}]", new Object[] { rtIP.getAddress().getHostAddress(), Integer.valueOf(rtIP.getPort()) });
//                if (this.threadID > 999) {
//                    this.threadID = 0;
//                }
                log.info("###");
                processService(connectedSocket, dest);
            }
//             return;
        } catch (Exception e) {
            if ((!(e instanceof SocketException)) || (!isClosed)) {
                log.error(" ===== startServer() Exception : {}", e.getMessage());
            }
        }
    }

    private void processService(Socket connectedSocket, String dest) {
        ServiceProcessor service = new ServiceProcessor(connectedSocket, dest);
        Thread internalThread = new Thread(service);
        internalThread.start();
    }

    public void shutDownServer() throws Exception {
        isClosed = true;
        this.server.close();
        log.trace("shutDownServer {}", isClosed);
    }

    String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder();
        for (final byte b : a)
            sb.append(String.format("%02x ", b & 0xff));
        return sb.toString();
    }
}
