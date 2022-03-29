//package com.danusys.web.platform.test;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.util.Map;
//
//@Service
//public class TestCommons {
//	private final Logger LOGGER = LoggerFactory.getLogger(TestCommons.class);
//	public final String HEADER_BODY_HEX = (char) 0xD0 + "";
//	public final String FIELD_HEX = (char) 0xD1 + "";
//	public final String ARRAY_HEX = (char) 0xD2 + "";
//	public final String ARRAY_FIELD_HEX = (char) 0xD3 + "";
//	public final String B_HEX = (char) 0xD4 + ""; // 미사용태그 정의 후 사용
//	public final String C_HEX = (char) 0xD5 + ""; // 미사용태그 정의 후 사용
//	public final String D_HEX = (char) 0xD6 + ""; // 미사용태그 정의 후 사용
//	public final String E_HEX = (char) 0xD7 + ""; // 미사용태그 정의 후 사용
//	public final String F_HEX = (char) 0xD8 + ""; // 미사용태그 정의 후 사용
//	public final String G_HEX = (char) 0xD9 + ""; // 미사용태그 정의 후 사용
//	public final String DATA_START_HEX = (char) 0xDA + "";
//	public final String DATA_END_HEX = (char) 0xDB + "";
//	public final String H_HEX = (char) 0xDC + ""; // 미사용태그 정의 후 사용
//	public final String I_HEX = (char) 0xDD + ""; // 미사용태그 정의 후 사용
//	public final String J_HEX = (char) 0xDE + ""; // 미사용태그 정의 후 사용
//	public final String END_HEX = (char) 0xDF + "";
//
//	public String setHeader(String timeStamp17, int bodyLength) {
//		LOGGER.debug("setHeader Start !!");
//		System.out.println("setHeader Start !!");
//		String timeStamp14 = timeStamp17.substring(0, 14);
//		String header = "";
//		header = "EVENT" + FIELD_HEX;
//		header += "14" + FIELD_HEX;
//		header += "16" + FIELD_HEX;
//		//header += "17" + FIELD_HEX;
//		header += "1" + ARRAY_HEX + "1" + FIELD_HEX;
//		header += "1" + FIELD_HEX;
//		header += "P4800000000" + FIELD_HEX;
//		header += "B4812000000" + FIELD_HEX;
//		header += "N" + FIELD_HEX;
//		header += "TCP" + FIELD_HEX;
//		header += "" + FIELD_HEX;
//		header += timeStamp14 + FIELD_HEX;
//		header += bodyLength + FIELD_HEX;
//		header += "112" + FIELD_HEX;
//		header += "112" + ARRAY_HEX + "P4800000000" + ARRAY_HEX + "B4812000000" +  FIELD_HEX;
//		return header;
//	}
//
//	public String setBody(Map<String,Object > data) {
//		LOGGER.debug("setBody Start !!");
//		String evtOcrNo = data.get("evtOcrNo").toString();
//		String evtId = data.get("evtId").toString();
//		String evtNm = data.get("evtNm").toString();
//		String evtGradCd = data.get("evtGradCd").toString();
//		String evtPrgrsCd = data.get("evtPrgrsCd").toString();
//		String evtPlace = data.get("evtPlace").toString();
//		String evtDtl = data.get("evtDtl").toString();
//		String evtOcrYmdHms = data.get("evtOcrYmdHms").toString();
//		Double lon = Double.parseDouble(data.get("lon").toString());
//		Double lat = Double.parseDouble(data.get("lat").toString());
//		String evtPrgrsContent = data.get("evtPrgrsContent").toString();
//		String userId = data.get("userId").toString();
//
//		String itemVal = "OCCUR_FCLT_ID" + ARRAY_HEX + evtOcrNo + ARRAY_HEX;
//		itemVal += "IMAGE_SEND_TY_CD" + ARRAY_HEX + "FTP" + ARRAY_HEX;
//		String timeStamp14 = evtOcrYmdHms.substring(0, 14);
//		String body = DATA_START_HEX;
////		body += "112UC001" + FIELD_HEX;
////		body += "112시스템" + FIELD_HEX;
////		body += "20" + FIELD_HEX;
////		body += timeStamp17 + FIELD_HEX;
////		body += "10" + FIELD_HEX;
////		body += "128.574370" + ARRAY_HEX + "35.198278" + ARRAY_HEX + "0" + FIELD_HEX;
////		body += "경상남도 창원시 마산합포구 마산소방서" + FIELD_HEX;
////		body += "강도사건" + FIELD_HEX;
//
//		body += evtId + FIELD_HEX;
//		body += evtNm + FIELD_HEX;
//		body += evtGradCd + FIELD_HEX;
//		body += evtOcrYmdHms + FIELD_HEX;
//		body += evtPrgrsCd + FIELD_HEX;
//		body += lon + ARRAY_HEX + lat + ARRAY_HEX + "0" + FIELD_HEX;
//		body += evtPlace + FIELD_HEX;
//		body += evtDtl + FIELD_HEX;
//		body += timeStamp14 + FIELD_HEX;
//		//여기부터 진행
//		body += evtPrgrsContent + FIELD_HEX;
//		body += userId + FIELD_HEX;
//		body += timeStamp14 + FIELD_HEX;
//		body += "" + FIELD_HEX;
//
//		// 이벤트 항목 수
//		body += "2" + FIELD_HEX; // ITEM
//		body += itemVal + FIELD_HEX; // ITEMVAL
//
//		//이벤트 항목명 값 , 이벤트 세부분류 코드 통플에선 사용하지 않음
//		body += "위험지역접근" + ARRAY_HEX + "B1144000000";
//		//body += "J1000000000" + ARRAY_HEX + "B1144000000" + ARRAY_HEX + "B1144000000ESE";
//		body += DATA_END_HEX;
//		body += END_HEX;
//		String header = setHeader(evtOcrYmdHms, body.length());
//		LOGGER.debug("setBody End !!");
//		return header + HEADER_BODY_HEX + body;
//	}
//
//}
