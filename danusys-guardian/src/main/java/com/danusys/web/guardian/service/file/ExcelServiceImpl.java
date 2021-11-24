package com.danusys.web.guardian.service.file;

import com.danusys.web.guardian.model.BaseDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;

@Service
public class ExcelServiceImpl implements ExcelService {

//	@Resource(name= "excelUpFacilityService")
//	private EgovExcelService excelUpService;

	private BaseDao baseDao;

	@Autowired
	public ExcelServiceImpl(BaseDao baseDao) {
//		this.excelUpService = excelUpService;
		this.baseDao = baseDao;
	}
//
//	public void saveExcelCctv(String sqlid, InputStream file) throws Exception {
//		excelUpService.uploadExcel(sqlid, file, 1, 0);
//	}
//
//	public void saveExcelPOICctv(String sqlid, InputStream file) throws Exception {
//		excelUpService.uploadExcel(sqlid, file, 1, 0, new XSSFWorkbook());
//	}

	public Map<String,Object> excelDownLoad(String sqlid, HttpServletRequest request) throws Exception{
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		Map<String, Object> param = new HashMap<String, Object>();

		param.put("firstIndex", 0);
		param.put("recordCountPerPage", "-1");

		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String key = (String) enu.nextElement();
			param.put(key, URLDecoder.decode(request.getParameter(key), "UTF-8"));
		}

		list = baseDao.baseSelectList(sqlid, param);

		List<Map<String, String>> headerList = new ArrayList<Map<String, String>>();
		int headerCnt = Integer.parseInt(request.getParameter("headerCnt"));

		for (int i = 0; i < headerCnt; i++) {
			Map<String, String> header = new HashMap<String, String>();
			String headerText = request.getParameter("headerText" + i);
			String headerField = request.getParameter("headerField" + i);
			header.put("headerText", headerText);
			header.put("headerField", headerField);
			headerList.add(header);
		}
		map.put("result", list);
		map.put("headerList", headerList);
		return map;
	}
}
