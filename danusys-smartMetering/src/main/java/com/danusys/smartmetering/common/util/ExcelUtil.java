/*package com.danusys.smartmetering.common.util;

import java.util.List;
import java.util.Map;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;

import com.danusys.smartmetering.common.dao.CommonDao;

@Component
public class ExcelUtil {

@Autowired
CommonDao commonDao;


public void excelDownload(Map<String, Object> header, String qId, Map<String, Object> paramMap, HttpServletResponse response) throws Exception{
    XSSFWorkbook xssfWb = null;
    XSSFSheet xssfSheet = null;
    XSSFRow xssfRow = null;
    XSSFCell xssfCell = null;

    xssfWb = new XSSFWorkbook();
    xssfSheet = xssfWb.createSheet("워크 시트1");

    XSSFFont font = xssfWb.createFont();
    font.setFontName(HSSFFont.FONT_ARIAL);
    font.setBold(true);

    //헤더 셀 스타일 지정
    CellStyle headerCellStyle = xssfWb.createCellStyle();
    headerCellStyle.setFont(font);
    headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    headerCellStyle.setBorderBottom((short) 2);
    headerCellStyle.setBorderLeft((short) 2);
    headerCellStyle.setBorderRight((short) 2);
    headerCellStyle.setBorderTop((short) 2);
    headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

    int rowNo = 0;
    int headerCellIndex = 0;
    xssfRow = xssfSheet.createRow(rowNo);

    //헤더 셀 생성
    for( String headerKey : header.keySet()) {
        xssfCell = xssfRow.createCell(headerCellIndex);
        xssfCell.setCellValue(header.get(headerKey).toString());
        xssfCell.setCellStyle(headerCellStyle);
        headerCellIndex++;
    }
    rowNo++;

    List<Map<String, Object>> rowMap = commonDao.selectList(qId, paramMap);

    //로우 셀 스타일 지정
    CellStyle rowCellStyle = xssfWb.createCellStyle();
    rowCellStyle.setBorderBottom((short) 1);
    rowCellStyle.setBorderLeft((short) 1);
    rowCellStyle.setBorderRight((short) 1);
    rowCellStyle.setBorderTop((short) 1);

    //로우 셀 생성
    for( Map<String, Object> rowData : rowMap ) {
        xssfRow = xssfSheet.createRow(rowNo);
        int rowCellIndex = 0;
        for( String headerKey : header.keySet()) {
            xssfCell = xssfRow.createCell(rowCellIndex);
            xssfCell.setCellValue((String)rowData.get(headerKey));
            xssfCell.setCellStyle(rowCellStyle);
            rowCellIndex++;
        }
        rowNo++;
    }

    for (int i = 0; i < header.size(); i++) {
        xssfSheet.autoSizeColumn(i);
        xssfSheet.setColumnWidth(i, (xssfSheet.getColumnWidth(i))+512 );
    }

    response.reset();
    response.setHeader("Content-Disposition", "attachment;filename=excelFileName.xls");
    response.setContentType("application/vnd.ms-excel");

    OutputStream out = new BufferedOutputStream(response.getOutputStream());

    try {
        xssfWb.write(out);
        out.flush();
    } catch(Exception e) {
        e.printStackTrace();
    } finally {
        if(out != null) out.close();
    }
}


public XSSFWorkbook createExcelDoc(Map<String, Object> paramMap) throws Exception {
    XSSFWorkbook xssfWb = null;
    XSSFSheet xssfSheet = null;
    XSSFRow xssfRow = null;
    XSSFCell xssfCell = null;

    xssfWb = new XSSFWorkbook();
    xssfSheet = xssfWb.createSheet("Sheet");

    XSSFFont font = xssfWb.createFont();
    font.setFontName(HSSFFont.FONT_ARIAL);
    font.setBold(true);

    // Settings
    String qId = paramMap.get("qId").toString();
    String columnArr = paramMap.get("columnArr").toString();
    String columnNmArr = paramMap.get("columnNmArr").toString();

    //헤더 셀 스타일 지정
    CellStyle headerCellStyle = xssfWb.createCellStyle();
    headerCellStyle.setFont(font);
    headerCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
    headerCellStyle.setBorderBottom((short) 2);
    headerCellStyle.setBorderLeft((short) 2);
    headerCellStyle.setBorderRight((short) 2);
    headerCellStyle.setBorderTop((short) 2);
    headerCellStyle.setFillForegroundColor(HSSFColor.LIGHT_CORNFLOWER_BLUE.index);
    headerCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

    int rowNo = 0;
    int headerCellIndex = 0;
    xssfRow = xssfSheet.createRow(rowNo);

    //헤더 셀 생성
    for( String columnNm : columnNmArr.split("\\|")) {
        xssfCell = xssfRow.createCell(headerCellIndex);
        xssfCell.setCellValue(columnNm);
        xssfCell.setCellStyle(headerCellStyle);
        headerCellIndex++;
    }
    rowNo++;

    List<Map<String, Object>> rowMap = commonDao.selectList(qId, paramMap);

    //로우 셀 스타일 지정
    CellStyle rowCellStyle = xssfWb.createCellStyle();
    rowCellStyle.setBorderBottom((short) 1);
    rowCellStyle.setBorderLeft((short) 1);
    rowCellStyle.setBorderRight((short) 1);
    rowCellStyle.setBorderTop((short) 1);

    //로우 셀 생성
    for( Map<String, Object> rowData : rowMap ) {
        xssfRow = xssfSheet.createRow(rowNo);
        int rowCellIndex = 0;

        for( String column : columnArr.split("\\|")) {
            xssfCell = xssfRow.createCell(rowCellIndex);
            xssfCell.setCellValue((String)rowData.get(column));
            xssfCell.setCellStyle(rowCellStyle);
            rowCellIndex++;
        }
        rowNo++;
    }

    for (int i = 0; i < columnArr.split("\\|").length; i++) {
        xssfSheet.autoSizeColumn(i);
        xssfSheet.setColumnWidth(i, Math.min(255 * 256, xssfSheet.getColumnWidth(i) + 1500));
    }
    return xssfWb;
}

public ModelAndView exportExcel(Map<String, Object> paramMap) throws Exception {
    ModelAndView mav = new ModelAndView();
    XSSFWorkbook xssfWb = this.createExcelDoc(paramMap);

    mav.setViewName("downloadView");
    mav.addObject("fileName", paramMap.get("fileName"));
    mav.addObject("fileStream", xssfWb);

    return mav;
}
}*/