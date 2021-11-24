package com.danusys.web.guardian.service.file;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Map;

public interface ExcelService {
	
//	public void saveExcelCctv(String sqlid, InputStream file) throws Exception;
//    public void saveExcelPOICctv(String sqlid, InputStream file) throws Exception;
    
    
    public Map<String,Object> excelDownLoad(String sqlid, HttpServletRequest request) throws Exception;
}
