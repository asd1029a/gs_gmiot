
package com.danusys.web.smartmetering.common.util;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.danusys.web.smartmetering.common.dao.CommonDao;

@Component
public class PagingUtil {

	//private int pageIndex = 1;
	//private int pageRowSize = 10;
	//private int pageSize = 10;
	//private int firstRowIndex = 1;
	//private int lastRowIndex = 1;

	//private String prevPageBtn = "";
	//private String nextPageBtn = "";
	
	@Autowired
	CommonDao commonDao;
	

/*
	public String getPageHtml(Map<?, ?> map) {
		StringBuffer sb = new StringBuffer();

		int pageIndex = Integer.parseInt(map.get("pageIndex").toString());
		int firstPageNo = Integer.parseInt(map.get("firstPageNo").toString());
		int lastPageNo = Integer.parseInt(map.get("lastPageNo").toString());
		
		sb.append("<nav aria-label=\"...\">\n");
		sb.append("\t<ul class=\"pagination justify-content-center\">\n");

		if (map.get("prevPageBtn").equals("disabled"))
			sb.append("\t\t<li class=\"page-item disabled\"><a class=\"page-link\" href=\"#\" tabindex=\"-1\">이전</a></li>\n");
		else {
			sb.append("\t\t<li class=\"page-item\"><a class=\"page-link\" href=\"javascript:;\" onclick=\"comm.linkPage('" + (firstPageNo - 1)
					+ "');\">이전</a></li>\n");
		}
		for (int i = firstPageNo; i <= lastPageNo; i++) {
			sb.append("\t\t<li class=\"page-item" + (i == pageIndex ? " active" : " ")
					+ "\"><a href=\"javascript:;\" class=\"page-link\" onclick=\"comm.linkPage('" + i + "');\">" + i + "</a><span class=\"sr-only\">(current)</span></li>\n");
		}
		if (map.get("nextPageBtn").equals("disabled"))
			sb.append("\t\t<li class=\"page-item disabled\"><a class=\"page-link\" href=\"#\" tabindex=\"-1\">다음</a></li>\n");
		else {
			sb.append("\t\t<li class=\"page-item\"><a class=\"page-link\" href=\"javascript:;\" onclick=\"comm.linkPage('" + (lastPageNo + 1)
					+ "');\">다음</a></li>\n");
		}
		sb.append("\t</ul>\n");
		sb.append("</nav>");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("pageIndex", pageIndex);
		resultMap.put("firstPageNo", firstPageNo);
		resultMap.put("lastPageNo", lastPageNo);
		
		return sb.toString();
	}
	*//*


	*/
/*
	 * public Map<String, Object> getSettingMap(Map<String, Object> map) { int
	 * totalRowCnt = Integer.parseInt((String) map.get("totalRowCnt"));
	 * 
	 * if ((map.containsKey("pageSize")) && (!map.get("pageSize").equals(""))) {
	 * pageSize = Integer.parseInt(map.get("pageSize").toString()); } if
	 * ((map.containsKey("pageIndex")) && (!map.get("pageIndex").equals(""))) {
	 * pageIndex = Integer.parseInt(map.get("pageIndex").toString()); } if
	 * ((map.containsKey("pageRowSize")) && (!map.get("pageRowSize").equals(""))) {
	 * pageRowSize = Integer.parseInt(map.get("pageRowSize").toString()); } int
	 * totalPageCnt = ((totalRowCnt - 1) / pageRowSize + 1); int firstPageNo =
	 * ((pageIndex - 1) / pageSize * pageSize + 1); int lastPageNo = (firstPageNo +
	 * pageSize - 1);
	 * 
	 * if (firstPageNo==1) { prevPageBtn = "disabled"; } else { prevPageBtn = ""; }
	 * 
	 * if ((lastPageNo > totalPageCnt) || (lastPageNo == totalPageCnt)) { lastPageNo
	 * = totalPageCnt; nextPageBtn = "disabled"; } else { nextPageBtn = ""; }
	 * 
	 * firstRowIndex = ((pageIndex - 1) * pageRowSize); lastRowIndex = (pageIndex *
	 * pageRowSize);
	 * 
	 * map.put("pageIndex", Integer.valueOf(pageIndex)); map.put("firstRowIndex",
	 * Integer.valueOf(firstRowIndex)); map.put("lastRowIndex",
	 * Integer.valueOf(lastRowIndex)); map.put("firstPageNo",
	 * Integer.valueOf(firstPageNo)); map.put("lastPageNo",
	 * Integer.valueOf(lastPageNo)); map.put("pageRowSize",
	 * Integer.valueOf(pageRowSize)); map.put("totalPageCnt",
	 * Integer.valueOf(totalPageCnt)); map.put("prevPageBtn", prevPageBtn);
	 * map.put("nextPageBtn", nextPageBtn);
	 * 
	 * return map; }
	 * 
	 * public Map<String, Object> getPagingData(Map<String, Object> paramMap) throws
	 * Exception { String qid = paramMap.get("qid").toString(); Map<String, Object>
	 * resultMap = new HashMap<String, Object>(); paramMap.put("pagePre", "Y");
	 * pageIndex = (int) paramMap.get("selectedPage"); pageRowSize = (int)
	 * paramMap.get("dataPerPage");
	 * 
	 * List<Map<String, Object>> cntList = commonDao.selectList(qid, paramMap);
	 * if(pageIndex == 1) { firstRowIndex = 0; } else { firstRowIndex = ((pageIndex
	 * - 1) * pageRowSize); } lastRowIndex = (pageIndex * pageRowSize);
	 * paramMap.put("firstRowIndex",firstRowIndex);
	 * paramMap.put("lastRowIndex",lastRowIndex); if(cntList.size() == 0) {
	 * paramMap.put("totalRowCnt", "0"); } else { paramMap.put("totalRowCnt",
	 * cntList.get(0).get("totalRowCnt").toString()); } paramMap.put("pagePre",
	 * "N");
	 * 
	 * List<Map<String, Object>> list = commonDao.selectList(qid, paramMap);
	 * resultMap.put("data", list); resultMap.put("pagingParam", paramMap);
	 * 
	 * return resultMap; }
	 */

	
	public Map<String, Object> getSettingMap(String qId, Map<String, Object> paramMap) throws Exception {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("data", commonDao.selectList(qId, paramMap));
		resultMap.put("draw", paramMap.get("draw"));
		
		Integer cnt = commonDao.selectOneObject(qId.concat("_CNT"), paramMap);
		resultMap.put("recordsTotal", cnt.intValue());
		resultMap.put("recordsFiltered", cnt.intValue());
		return resultMap;
	}
}
