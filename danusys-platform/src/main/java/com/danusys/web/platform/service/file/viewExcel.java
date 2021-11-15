package com.danusys.web.platform.service.file;

import org.apache.commons.codec.binary.Base64;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class viewExcel extends AbstractView {

	private static final Logger log  = LoggerFactory.getLogger(viewExcel.class);

	private static final String CONTENT_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

	/**
	 * 엑셀파일을 설정하고 생성한다.
	 * @param model
	 * @param wb
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	protected void buildExcelDocument(Map<String, Object> model, XSSFWorkbook wb, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        String today = (new SimpleDateFormat("yyyyMMddHHmmss").format(date));
		String fileName = request.getParameter("fileName") +"_"+ today;
		
        response.setHeader("Content-Disposition", "attachment; filename="+ fileName +".xlsx");
        setContentType("application/download; utf-8");
        
        String img = "";
        if(request.getParameter("img")!=null) {
        	img = URLDecoder.decode(request.getParameter("img"), "UTF-8");
        }
        
        XSSFCell cell = null;

        log.debug("### buildExcelDocument start !!!");

        XSSFSheet sheet = wb.createSheet(fileName);
        
        //cell 테두리 + 가운데 정렬
        final XSSFCellStyle style = wb.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
        /*style.setBorderBottom(XSSFCellStyle.BORDER_THIN);
        style.setBorderRight(XSSFCellStyle.BORDER_THIN);
        style.setBorderLeft(XSSFCellStyle.BORDER_THIN);
        style.setBorderTop(XSSFCellStyle.BORDER_THIN);
        style.setVerticalAlignment(XSSFCellStyle.VERTICAL_CENTER);*/
        style.setWrapText(true);
        /*style.setAlignment(XSSFCellStyle.ALIGN_CENTER);*/
        style.setAlignment(HorizontalAlignment.CENTER);
        
        
        List<Map<String, String>> headers =  (List<Map<String, String>>) model.get("headerList");
		
		int inx = 0;

        for (int i = 0; i < headers.size(); i++) {
            sheet.setColumnWidth(i, 30*128);
        	Map<String, String> headerMap = headers.get(i);
        	String headerText = headerMap.get("headerText");
        	headerText = URLDecoder.decode(headerText,"UTF-8");
        	System.out.println(headerMap.get("headerField"));
        	System.out.println(headerText);
		    cell = getCell(sheet, 0, inx++);
		    cell.setCellStyle(style);
			setText(cell, headerText);
		}
        

		List<Map<String, Object>> list = (List<Map<String, Object>>) model.get("result");

		for (int i = 0; i < list.size(); i++) {
	        inx = 0;
			
	        Map<String, Object> listMap = list.get(i);
	        
			for (int j = 0; j < headers.size(); j++) {
				Map<String, String> header = headers.get(j);
			    String field = header.get("headerField");
				
				String value = "";
				if(listMap.get(field) != null){
					value = listMap.get(field).toString();
				}
				cell = getCell(sheet, 1 + i, inx++);
				cell.setCellStyle(style);
				setText(cell, value);
			}
		}
        
        if(!img.isEmpty()) {
        	setImage(wb, img, list.size() + 1);
        }
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		wb.write(baos);
		byte[] temp = baos.toByteArray();
		
        OutputStream out = response.getOutputStream();
        out.write(temp);
        
		out.close();
		baos.close();
	}
	
	// 엑셀에 이미지 시트 추가
	private void setImage(XSSFWorkbook wb, String img, int row) {
        XSSFSheet sheet = wb.createSheet("이미지");
        
        String prefix = "base64";
        int startIndex = img.indexOf(prefix) + prefix.length();
        byte[] imgData1 = Base64.decodeBase64(img.substring(startIndex));
        
        int pictureIndex = wb.addPicture(imgData1, wb.PICTURE_TYPE_PNG);
        
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        
        XSSFCreationHelper helper = wb.getCreationHelper();
        XSSFClientAnchor anchor = helper.createClientAnchor();
        
        anchor.setCol1(0);
        anchor.setCol2(0);
        anchor.setRow1(0);
        anchor.setRow2(0);
        
        XSSFPicture picture = drawing.createPicture(anchor, pictureIndex);
        
        picture.resize();
	}

	@Override
	protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		XSSFWorkbook workbook = new XSSFWorkbook();
//		LOGGER.debug("Created Excel Workbook from scratch");

		setContentType(CONTENT_TYPE_XLSX);

		buildExcelDocument(model, workbook, request, response);

		// Set the filename
		String sFilename = "";
		if(model.get("filename") != null){
			sFilename = (String)model.get("filename");
		}else if(request.getAttribute("filename") != null){
			sFilename = (String)request.getAttribute("filename");
		}else{
			sFilename = getClass().getSimpleName();
		}

		// Set the content type.
		response.setContentType(getContentType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + sFilename + ".xlsx\"");

		// Flush byte array to servlet output stream.
		ServletOutputStream out = response.getOutputStream();
		out.flush();
		workbook.write(out);
		out.flush();

		// Don't close the stream - we didn't open it, so let the container handle it.
		// http://stackoverflow.com/questions/1829784/should-i-close-the-servlet-outputstream
	}

	protected XSSFCell getCell(XSSFSheet sheet, int row, int col) {
		XSSFRow sheetRow = sheet.getRow(row);
		if (sheetRow == null) {
			sheetRow = sheet.createRow(row);
		}
		XSSFCell cell = sheetRow.getCell((short) col);
		if (cell == null) {
			cell = sheetRow.createCell((short) col);
		}
		return cell;
	}

	protected void setText(XSSFCell cell, String text) {
		cell.setCellType(XSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(text);
	}

}
