package com.danusys.web.smartmetering.common.view;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.servlet.view.AbstractView;

public class DownloadView extends AbstractView {

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String fileName = model.get("fileName").toString();
        String userAgent = request.getHeader("User-Agent");
        
        if(userAgent.indexOf("MSIE") > -1 || userAgent.indexOf("Trident") > -1){
            fileName = URLEncoder.encode(fileName, "utf-8").replaceAll("\\+", "%20");;
        } else if(userAgent.indexOf("Chrome") > -1) {
        	StringBuffer sb = new StringBuffer();
        	for(int i=0; i<fileName.length(); i++) {
        		char c = fileName.charAt(i);
        		if(c > '~') {
        			sb.append(URLEncoder.encode(""+c, "UTF-8"));
        		}else {
        			sb.append(c);
        		}
        	}
        	fileName = sb.toString();
        } else {
        	fileName = new String(fileName.getBytes("utf-8"), "ISO-8859-1");
        }
        
        //response.setContentLength((int)file.length());
        response.setContentType(getContentType());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
        
        XSSFWorkbook xssfWb = (XSSFWorkbook) model.get("fileStream");
        
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        xssfWb.write(os);
        os.flush();
        os.close();
	}
}