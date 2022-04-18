package com.danusys.web.smartmetering.account.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.danusys.web.smartmetering.common.dao.CommonDao;
import com.danusys.web.smartmetering.common.util.JsonUtil;
import com.danusys.web.smartmetering.common.util.PagingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.danusys.web.smartmetering.account.service.AccountService;
import com.danusys.web.smartmetering.common.service.CommonService;

@Service
public class AccountServiceImpl implements AccountService {

	@Autowired
	CommonDao commonDao;
	@Autowired
	PagingUtil pagingUtil;
	
	@Autowired
	CommonService commonService;
	
	/**
	 * ################
	 * 수용가
	 * ################ 
	 */

	/**
	 * 수용가 : 목록 조회
	 */
	@Override
	public String selectListAccount(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("account.SELECT_LIST_ACCOUNT", paramMap));
	}
	
	/**
	 * 수용가 및 단말기 정보 조회 (API)
	 */
	@Override
	public int insertAccount(Map<String, Object> paramMap) throws Exception {
		return commonDao.insert("account.INSERT_ACCOUNT", paramMap);
	}
	
	/**
	 *  관제 수용가 리스트 조회
	 */
	@Override
	public String selectListAccountGIS(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_GIS", paramMap));
	}
	
	/**
	 * 수용가 검침 데이터 조회 (API)
	 */
	@Override
	public int insertAccountData(Map<String, Object> paramMap) throws Exception {
		return commonDao.insert("account.INSERT_ACCOUNT_DATA", paramMap);
	}

	/**
	 * 수용가 : geojson 목록 조회 
	 */
	@Override
	public String selectListAccountGeojson(Map<String, Object> paramMap) throws Exception {
		List<Map<String, Object>> geoList = commonDao.selectList("account.SELECT_LIST_ACCOUNT_FOR_LAYER", paramMap);
		return JsonUtil.getJsonString(commonService.getGeoJson(geoList, "account"));		
	}
	
	/**
	 * 수용가 : 기초구역별 수용가 cnt 조회
	 */
	@Override
	public String selectListAccountCntInBaseArea(Map<String, Object> paramMap) throws Exception {
		List<Map<String,Object>> geoList = commonDao.selectList("account.SELECT_LIST_ACCOUNT_CNT_IN_BASE_AREA", paramMap);
		return JsonUtil.getJsonString(commonService.getGeoJson(geoList, "accountCnt"));
	}
	
	/**
	 * ################
	 * 검침 데이터
	 * ################
	 */
	
	/**
	 * 검침 : 목록 조회
	 */
	@Override
	public String selectListAccountData(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("account.SELECT_LIST_ACCOUNT_DATA", paramMap));
	}
	
	/**
	 * 수용가통계 수용가 사용량조회 
	 */
	@Override
	public String selectListAccountDataStats(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_STATS", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 장비 이벤트 (차트)
	 */
	public String selectListAccountDataForDeviceEventChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_FOR_DEVICE_EVENT_CHART", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 시간별 증가량 (차트)
	 */
	public String selectListAccountDataForTimediffChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_FOR_TIMEDIFF_CHART", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 월별 증가량 (차트)
	 */
	public String selectListAccountDataForMonthdiffChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_FOR_MONTHDIFF_CHART", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 일별 증가량 (차트)
	 */
	public String selectListAccountDataForDaydiffChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_FOR_DAYDIFF_CHART", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 최대, 최소 수용가
	 */
	public String selectListAccountDataMinMax(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_MIN_MAX", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 총 사용량, 평균 사용량
	 */
	public String selectListAccountDataSumAvg(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectOneObject("account.SELECT_DETAIL_ACCOUNT_DATA_SUM_AVG", paramMap));
	}
	
	/**
	 * 수용가 : 검침 데이터 - 수도 총 사용량 TOP
	 */
	public String selectListAccountDataTop(Map<String, Object> paramMap) throws Exception {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		// 최저 Top10 
		resultMap.put("topAsc", commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_TOP", paramMap));
		
		// 최고 Top10 
		paramMap.put("orderType", "DESC");
		resultMap.put("topDesc", commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_TOP", paramMap));
		
		return JsonUtil.getJsonString(resultMap);
	}
	
	/**
	 * 수용가 : 검침 데이터 - 날짜별 평균 증가 - 라인차트
	 */
	public String selectListAccountDataStatsChart(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_DATA_STATS_CHART", paramMap));
	}

	
	/**
	 * ################
	 * 수용가 그룹
	 * ################ 
	 */
	
	/**
	 * 수용가 그룹 : 목록 조회
	 */
	@Override
	public String selectListAccountGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("account.SELECT_LIST_ACCOUNT_GROUP", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 소속 수용가 목록 조회
	 */
	@Override
	public String selectListAccountInGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("account.SELECT_LIST_ACCOUNT_IN_GROUP", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 등록 
	 */
	@Override
	public String insertAccountGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.insert("account.INSERT_ACCOUNT_GROUP", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 수정
	 */
	@Override
	public String updateAccountGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.update("account.UPDATE_ACCOUNT_GROUP", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 삭제
	 */
	@Override
	public String deleteAccountGroup(Map<String, Object> paramMap) throws Exception {
		commonDao.delete("account.DELETE_ACCOUNT_IN_ACCOUNT_GROUP_GROUP_SEQ", paramMap);
		return JsonUtil.getCntJsonString(commonDao.delete("account.DELETE_ACCOUNT_GROUP", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 소속 수용가 등록
	 */
	@Override
	public String insertAccountInGroup(Map<String, Object> paramMap) throws Exception {
		commonDao.delete("account.DELETE_ACCOUNT_IN_ACCOUNT_GROUP_GROUP_SEQ", paramMap);
		return JsonUtil.getCntJsonString(commonDao.insert("account.INSERT_ACCOUNT_IN_ACCOUNT_GROUP", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 소속 수용가 해제
	 */
	@Override
	public String deleteAccountInGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.delete("account.DELETE_ACCOUNT_IN_ACCOUNT_GROUP_ACCOUNT_NO", paramMap));
	}
	
	/**
	 * 수용가 그룹 : 소속수용가 체크 조회
	 */
	@Override
	public String selectListAccountInGroupCheck(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("account.SELECT_LIST_ACCOUNT_IN_GROUP_CHECK", paramMap));
	}
}