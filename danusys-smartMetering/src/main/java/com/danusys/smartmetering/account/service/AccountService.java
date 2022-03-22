package com.danusys.smartmetering.account.service;

import java.util.Map;

public interface AccountService {
	/**
	 * ################
	 * 수용가
	 * ################ 
	 */
	public int insertAccount(Map<String, Object> paramMap) throws Exception;
	public int insertAccountData(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountGeojson(Map<String, Object> paramMap)throws Exception;
	public String selectListAccount(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountInGroupCheck(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountData(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataForDeviceEventChart(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataForTimediffChart(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataForMonthdiffChart(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataForDaydiffChart(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataMinMax(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataSumAvg(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataTop(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountCntInBaseArea(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountGIS(Map<String,Object> paramMap) throws Exception;
	public String selectListAccountDataStats(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountDataStatsChart(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * ################
	 * 수용가 그룹
	 * ################ 
	 */
	public String selectListAccountGroup(Map<String, Object> paramMap) throws Exception;
	public String selectListAccountInGroup(Map<String, Object> paramMap) throws Exception;
	public String insertAccountGroup(Map<String, Object> paramMap) throws Exception;
	public String updateAccountGroup(Map<String, Object> paramMap) throws Exception;
	public String deleteAccountGroup(Map<String, Object> paramMap) throws Exception;
	public String insertAccountInGroup(Map<String, Object> paramMap) throws Exception;
	public String deleteAccountInGroup(Map<String, Object> paramMap) throws Exception;
}