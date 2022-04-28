package com.danusys.web.smartmetering.event.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.danusys.web.smartmetering.common.dao.CommonDao;
import com.danusys.web.smartmetering.common.service.CommonService;
import com.danusys.web.smartmetering.common.util.DateUtil;
import com.danusys.web.smartmetering.common.util.JsonUtil;
import com.danusys.web.smartmetering.common.util.PagingUtil;
import com.danusys.web.smartmetering.common.util.StringUtil;
import com.danusys.web.smartmetering.event.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {

	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	CommonDao commonDao;
	
	@Autowired
	PagingUtil pagingUtil;
	
	@Autowired
    CommonService commonService;
	
	
	/**
	 * 이벤트 : 목록 조회
	 */
	@Override
	public String selectListEvent(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("event.SELECT_LIST_EVENT", paramMap));
	}
	
	/**
	 * 이벤트 :GEOJSON 목록 조회
	 */
	@Override
	public String selectListEventGeojson(Map<String, Object> paramMap) throws Exception {
		List<Map<String,Object>> geoList = commonDao.selectList("event.SELECT_LIST_EVENT_FOR_LAYER", paramMap);
		System.out.println("이벤트목록 : " + geoList);

		return JsonUtil.getJsonString(commonService.getGeoJson(geoList, "event"));
	}

	/**
	 * 이벤트 : 이벤트 종료
	 */
	@Override
	public String updateEventStep(Map<String, Object> paramMap) throws Exception {
		Integer cnt = commonDao.insert("event.UPDATE_EVENT_STEP", paramMap);
		
		String result= "";
		if(cnt >0) {
			ArrayList<String> seqAry = new ArrayList();
			seqAry.add((String) paramMap.get("eventLogSeq"));
			paramMap.put("eventSeqAry", seqAry);
			result = JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_GIS", paramMap));
		}
		return result;
	}
	
	/**
	 * 이벤트 : 관제 이벤트 리스트 조회
	 */
	@Override
	public String selectListEventGIS(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_GIS", paramMap));
	}

	/**
	 * 이벤트 : 수용가별 최근 이벤트 조회
	 */
	@Override
	public String selectListLastestEventByAccount(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_LASTEST_EVENT_BY_ACCOUNT", paramMap));
	}

	/**
	 * 이벤트 : 한 수용가의 이벤트 로그조회
	 */
	@Override
	public String selectListEventLog(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_LOG", paramMap));
	}
	
	/**
	 * 이벤트 : 로그 등록
	 */
	@Override
	public int insertEventLog(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> resultList = commonDao.selectList("SELECT_LIST_ACCOUNT_DATA_EVENT", paramMap);
		
		int cnt = 0;
		for (Map<String, Object> resultMap : resultList) {
			resultMap.put("EVENT_NO", "EVT-"+ DateUtil.getCurrentDate("yyyyMMddHHmmsss")+"-"+ StringUtil.getRandomAlpha(5));
			try {
				commonDao.insert("event.INSERT_EVENT_LOG", resultMap);
				cnt++;
			} catch (Exception e) {
				logger.warn("accountNo, meterDtm, eventCode 중복");
			}
		}
		return cnt;
	}
	
	/**
	 * 이벤트 : 한 수용가의 이벤트 사용량 통계 조회
	 */
	@Override
	public String selectListEventDataStats(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_DATA_STATS", paramMap));
	}
	
	/**
	 * 이벤트 : 로그 처리 수정
	 */
	@Override
	public int updateEventLog(Map<String, Object> paramMap) throws Exception {
		return commonDao.insert("event.UPDATE_EVENT_LOG", paramMap);
	}
	
	/**
	 * 이벤트 : 전체 이벤트 발생 (도넛 차트)
	 */
	@Override
	public String selectListEventForTotalPerChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_FOR_TOTAL_PER_CHART", paramMap));
	}
	
	/**
	 * 이벤트 : 유형 별 발생 건수 (메인)
	 */
	public String selectDetailEventTotalCnt(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectOne("event.SELECT_DETAIL_EVENT_TOTAL_CNT", paramMap));
	}
	
	/**
	 * 이벤트 : 처리 현황 (도넛 차트)
	 */
	@Override
	public String selectListEventForProcessPerChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_FOR_PROCESS_PER_CHART", paramMap));
	}
	
	/**
	 * 이벤트 : 통계 (라인 차트)
	 */
	public String selectListEventForStatsChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("event.SELECT_LIST_EVENT_FOR_STATS_CHART", paramMap));
	}
}