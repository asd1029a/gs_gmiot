package com.danusys.web.guardian.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Properties;

@RestController
@RequestMapping("/api")
public class ApiRestController {

	@Value("${danusys.api.air.service.key}")
	private String serviceKey = "";
	
	@RequestMapping(value="/getMsrstnAcctoRltmMesureDnsty", method=RequestMethod.POST)
	public void getMsrstnAcctoRltmMesureDnsty(@RequestBody Map<String, Object> param,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
//		String serviceKey = config.getProperty("api.airServiceKey");
		String returnType = param.get("returnType").toString();
		String numOfRows = param.get("numOfRows").toString();
		String pageNo = param.get("pageNo").toString();
		String dataTerm = param.get("dataTerm").toString();
		String ver = param.get("ver").toString();
		String stationName = URLEncoder.encode(param.get("stationName").toString(), "UTF-8");
		
		String path = "http://apis.data.go.kr/B552584/ArpltnInforInqireSvc/getMsrstnAcctoRltmMesureDnsty";
		
		String queryParam = "?serviceKey=" + serviceKey;
		queryParam += "&returnType=" + returnType;
		queryParam += "&numOfRows=" + numOfRows;
		queryParam += "&pageNo=" + pageNo;
		queryParam += "&dataTerm=" + dataTerm;
		queryParam += "&ver=" + ver;
		queryParam += "&stationName=" + stationName;
		
		URL url = new URL(path + queryParam);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		
		StringBuilder sb = new StringBuilder();
		String t;
		
		while((t = br.readLine()) != null) {
			sb.append(t);
		}
		
		System.out.println(sb.toString());
		
		PrintWriter pw = response.getWriter();
		
		pw.write(sb.toString());
		
		pw.flush();
		pw.close();
		
	}
}
