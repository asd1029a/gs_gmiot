package com.danusys.guardian.service.cctv;

import com.danusys.guardian.model.BaseDao;
import com.danusys.guardian.socket.SocketClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Class Name : AgentServiceImpl.java
 * @Description : AgentServiceImpl class
 * @Modification Information
 *
 * @author 배선교
 * @since 2020-02-25
 * @version 1.0
 * @see
 *  
 */


@Service
public class CctvServiceImpl implements CctvService {
	private static Logger logger = LoggerFactory.getLogger(CctvServiceImpl.class);
	
	private BaseDao baseDao;
	private SocketClient socketClient;

	@Autowired
	public CctvServiceImpl(BaseDao baseDao, SocketClient socketClient) {
		this.baseDao = baseDao;
		this.socketClient = socketClient;
	}

	public Map<String, Object> makeCode(Map<String, Object> eventInfo) throws SQLException, IOException {
		Map<String, Object> TouringCode = new HashMap<String, Object>();
		String evtPrgrsCd = String.valueOf(eventInfo.get("evtPrgrsCd"));

		List<Map<String, Object>> list = baseDao.baseSelectList("facility.selectNearCctvList", eventInfo);
		if ("10".equals(evtPrgrsCd) || "30".equals(evtPrgrsCd)) {
			TouringCode.put("event_id", eventInfo.get("vmsStartCd"));
		} else if ("91".equals(evtPrgrsCd)) {
			TouringCode.put("event_id", eventInfo.get("vmsEndCd"));
		}

		for (int i = 0; list.size() > i; i++) {
			TouringCode.put("node_id_" + i, list.get(i).get("nodeId"));
		}
		TouringCode.put("contents", eventInfo.get("evtDtl"));
		TouringCode.put("code", "3600");
		TouringCode.put("svr_ip", list.get(0).get("vmsSvrIp"));

		SocketClient.msgSend(TouringCode);

		if ("10".equals(evtPrgrsCd) || "30".equals(evtPrgrsCd))
			sendPresetMove(list, eventInfo);
		return eventInfo;
	}
	
	public void sendPresetMove(List<Map<String, Object>> list,Map<String, Object> eventInfo) throws SQLException, IOException {
		
		int max = list.size();
		String[] selectRows = new String[max]; 
		for(int i=0;i<max;i++) {
			selectRows[i] = String.valueOf(list.get(i).get("fcltId"));
		}
		Map<String,Object> param = new HashMap<String, Object>();
		param.put("selectRowId", selectRows);
		param.put("lat",eventInfo.get("lat"));
		param.put("lon",eventInfo.get("lon"));
		 
		List<Map<String, Object>> presetList = baseDao.baseSelectList("facility.selectCctvPreset", param);
		max = presetList.size();
		
		for(int i=0;i<max;i++) {
			String cctvAgYn = String.valueOf(presetList.get(i).get("cctvAgYn"));
			if("0".equals(cctvAgYn)) continue;
			int presetNo = Integer.parseInt(presetList.get(i).get("presetNo").toString());
			if(presetNo<10) {
				sendSwPreset(presetList.get(i));
				continue;
			}
			sendHwPreset(presetList.get(i));
		}
	}
	
	public void sendSwPreset(Map<String, Object> map) throws SQLException, IOException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("code","1350");
		param.put("node_id",map.get("nodeId"));
		param.put("pan",map.get("pan"));
		param.put("tilt",map.get("tilt"));
		param.put("zoom",map.get("zoom"));
		param.put("focus",map.get("focus"));
		param.put("speed","60");
		param.put("svr_ip",map.get("vmsSvrIp"));
		SocketClient.msgSend(param);
	}
	
	public void sendHwPreset(Map<String, Object> map) throws SQLException, IOException {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("code","1200");
		param.put("node_id",map.get("nodeId"));
		param.put("presetNo",map.get("presetNo"));
		param.put("speed","60");
		param.put("svr_ip",map.get("vmsSvrIp"));
		SocketClient.msgSend(param);
	}

	@Override
	public void insertAllcenterSch(String xmlData) throws IOException, SQLException  {
	      Map<String, Object> param = null;
	      Map<String, Object> mapRet = new HashMap<String, Object>();
	      List<Map<String, Object>> resList =  new ArrayList<Map<String, Object>>();
			String resultMsg = "";
			Object value = null;
					
	         param = new HashMap<String, Object>();
	         int idx = xmlData.trim().indexOf("</ngvms:envelope>");
	         System.out.println("22222222"+idx);
	         String str = xmlData.substring(0, idx+17);
	   	        try
	   	        {
	   	        	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
	   	            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
	   	            
	   	            InputStream istream = new ByteArrayInputStream(str.getBytes("UTF-8"));
	   	            //doc.getDocumentElement().normalize();
	   	            Document doc = dBuilder.parse(istream);
	   	            //NodeList streamServerInfo = doc.getElementsByTagName("StreamServerInfo");
	   	            Element root = doc.getDocumentElement();
	   	            NodeList streamNodeInfo = doc.getElementsByTagName("StreamNodeInfo");
	   	            NodeList VMS_SVR_IP = doc.getElementsByTagName("VMS_SVR_IP");
	   	            String itemVMS_SVR_IP = VMS_SVR_IP.item(0).getFirstChild().getNodeValue();
	   	            
	   	            for (int i=0 ; i<streamNodeInfo.getLength() ; i++) {
	   	                Node streamNode = streamNodeInfo.item(i);
	   	                Element nodeElement = (Element)streamNode;
	   	                
	   	                NodeList baseInfoNodeList = nodeElement.getElementsByTagName("BaseInfo");
	   	                Node baseInfoNode = baseInfoNodeList.item(0);
	   	                Element baseElement = (Element)baseInfoNode;
	   	                String cctv_ag_yn = nodeElement.getElementsByTagName("UsePTZ").item(0).getFirstChild().getNodeValue();
	   	                String cctv_knd = nodeElement.getElementsByTagName("PTZPresetType").item(0).getFirstChild().getNodeValue();
	   	                String systemGroup = nodeElement.getElementsByTagName("SystemGroup").item(0).getFirstChild().getNodeValue();
	   	                String connect = baseElement.getAttribute("Connect");
	   	                String cctv_nm = baseElement.getAttribute("Name");
	   	                String node_id = baseElement.getAttribute("ID");
	   	                String management_code = baseElement.getAttribute("management_code");
	   	                String type = baseElement.getAttribute("Type");
	   	                String node_ip = baseElement.getAttribute("IP");
	   	                String node_port = baseElement.getAttribute("PORT");
	   	                mapRet.put("cctv_ag_yn", cctv_ag_yn);
	   	                if(cctv_knd.equals("CAMERA")){
	   	                	mapRet.put("cctv_knd", "HW");
	   	                }
	   	                else{
	   	                	mapRet.put("cctv_knd", "SW");
	   	                }
	   	                mapRet.put("connected", connect);
	   	                mapRet.put("type", type);
	   	                mapRet.put("cctv_nm", cctv_nm);
	   	                mapRet.put("node_id", node_id);
	   	                mapRet.put("node_ip", node_ip);
	   	                mapRet.put("management_code", management_code);
	   	                mapRet.put("node_port", node_port);
	   	                mapRet.put("fclt_purpose_cd",systemGroup);
	   	                
	   	                //logger.debug("===== item.i({}) end =================================================================== ", i);
	   	                /*if(itemVMS_SVR_IP.equals("200.1.1.220")){
	   	                	mapRet.put("fclt_purpose_cd", "POLICE");
	   	                }
	   	                if(itemVMS_SVR_IP.equals("200.0.31.130")){
	   	                	mapRet.put("fclt_purpose_cd", "JN");
	   	                }*/
	   	                mapRet.put("vms_svr_ip", itemVMS_SVR_IP);
	   	                mapRet.put("inst_id", "SYSTEM");
	   	                logger.debug("===== getAllCenterList >>>>> mapRet [{}]", mapRet);
	   	                try {
	   	                	//baseService.baseInsert("agent_info_map.insertAllCenter",param);
	   	                	
	   	                } catch (Exception ex) {
	   	    				logger.error("getAllCenterList insert Exception Message : {}", ex.toString());
	   	    	            //map.put("stat", ex.toString());
	   	    	            //errMsg = "ERROR";
	   	    			}
	   	                mapRet.clear();
	   	            }
	   	            
	   	        	resultMsg = "{\"cnt\":\""+itemVMS_SVR_IP+"\"}"; 
	   	        	System.out.println(resultMsg);
	   	            
	   	        }
	   	        catch (Exception ex)
	   	        {
	   	            logger.error(ex.toString());
	   	        }
	      
	      try {
				//baseService.baseDelete("agent_info_map.deleteAllCenter",param);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
}
