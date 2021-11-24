package com.danusys.web.guardian.controller;

import com.danusys.web.guardian.service.file.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

@RestController
public class ExcelRestController {
	private ExcelService excelService;

	@Autowired
	public ExcelRestController(ExcelService excelService) {
		this.excelService = excelService;
	}

	/*
	 * 엑셀업로드
	 */
	@PostMapping("/excelUpload/{sqlid}/action")
	public Map<String, String> excelUpload(@PathVariable("sqlid") String sqlid,
										   @RequestParam("files") MultipartFile[] files) {
		
//		final MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
//		final Map<String, MultipartFile> files = multiRequest.getFileMap();
		InputStream fis = null;

//		Iterator<Entry<String, MultipartFile>> itr = files.entrySet().iterator();
		Iterator<Entry<String, MultipartFile>> itr = (Iterator<Entry<String, MultipartFile>>) Arrays.asList(files);
		MultipartFile file;

    	Map<String , String> map = new HashMap<String , String>();
    	
    	try{
    		while (itr.hasNext()) {
    			Entry<String, MultipartFile> entry = itr.next();
    			
    			file = entry.getValue();
    			if (!"".equals(file.getOriginalFilename())) {
    				if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".XLS")) {
    					try {
    						fis = file.getInputStream();
//    						excelService.saveExcelCctv(sqlid, fis);
        		        	map.put("stat", "sucess");
    					} catch (Exception e) {
    			    		map.put("stat", e.toString());
    						throw e;
    					} finally {
    						if (fis != null) {
    							fis.close();
    						}
    					}

    				} else if (file.getOriginalFilename().endsWith(".xlsx") || file.getOriginalFilename().endsWith(".XLSX")){
    					try {
    						fis = file.getInputStream();
//    						excelService.saveExcelPOICctv(sqlid, fis);
    			        	map.put("stat", "sucess");
    					} catch (Exception e) {
    			    		map.put("stat", e.toString());
    						throw e;
    					} finally {
    						if (fis != null) {
    							fis.close();
    						}
    					}
    				} else {
    					map.put("stat", "File Type Error!!!");
    				}
    			}
    		}
    	} catch(Exception exx){
    		map.put("stat", exx.toString());
    	}
		return map;
	}
}