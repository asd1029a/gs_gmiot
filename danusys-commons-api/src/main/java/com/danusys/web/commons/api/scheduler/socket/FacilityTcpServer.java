package com.danusys.web.commons.api.scheduler.socket;

import com.danusys.web.commons.api.dto.CctvDTO;
import com.danusys.web.commons.api.dto.LogicalfolderDTO;
import com.danusys.web.commons.api.util.XmlDataUtil;
import com.danusys.web.commons.app.IOUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.List;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2022/06/16
 * Time : 5:56 PM
 */
@Slf4j
public class FacilityTcpServer {
    private int threadID = 0;
    private ServerSocket server;
    private int port = 8040;
    private static boolean isClosed = false;
    private static String dest = "";

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

                    log.info("subData {} ", subData);

//					this.agentController_.allCenterList(subData);
                    final CctvDTO cctvDTO = XmlDataUtil.getCctvInfo(subData);

                    log.info("###cctvDTO : {}", cctvDTO);


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
                shutDownServer();
            }
        }
    }

    public FacilityTcpServer() {
        log.debug(" ===== FacilityTcpServer Begin ===== ");
    }

    public static void main(String[] args) throws IOException {
        try {
            FacilityTcpServer server = new FacilityTcpServer();
            server.startServer();
        } catch (Exception e) {
            e.printStackTrace();
            log.error(" ===== TCPServerException : {}", e.getMessage());
        }
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
