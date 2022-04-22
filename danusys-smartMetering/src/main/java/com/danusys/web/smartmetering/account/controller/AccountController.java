package com.danusys.web.smartmetering.account.controller;

import com.danusys.web.smartmetering.account.service.AccountService;
import com.danusys.web.smartmetering.account.service.impl.AccountServiceImpl;
import com.danusys.web.smartmetering.common.annotation.JsonRequestMapping;
import com.danusys.web.smartmetering.common.util.DateUtil;
import com.danusys.web.smartmetering.common.util.ExcelUtil;
import com.danusys.web.smartmetering.schedule.AccountJobSchedule;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
public class AccountController {

	public AccountController(AccountService accountService){this.accountService=accountService;}

	private final AccountService accountService;
	@Autowired
    AccountJobSchedule accountJobSchedule;
	
	@Autowired
	ExcelUtil excelUtil;
	
	@RequestMapping("/account/test.do")
	public void test() throws Exception {
		accountJobSchedule.cronDeviceInfo();
	}
	
	@RequestMapping("/account/testSearch.do")
	public void testSearch() throws Exception {
		accountJobSchedule.cronMeterData();
	}
	
	@RequestMapping("/account/testInsertEvent.do")
	public void testInsertEvent() throws Exception {
		accountJobSchedule.cronEventData();
	}
	
	/**
	 * ################
	 * 수용가
	 * ################ 
	 */
	
	/**
	 * 조회/관리 수용가 페이지
	 */
	@RequestMapping(value="/search/account/accountList.do")
	public String accountList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "account/accountList";
	}
	
	/**
	 *  수용가 리스트 조회
	 */
	@JsonRequestMapping(value="/account/getListAccountGeojson.ado")
	public String getListAccountGeojson(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountGeojson(paramMap);
	}
	
	/**
	 *  관제 수용가 리스트 조회
	 */
	@JsonRequestMapping(value = "/account/getListAccountGIS.ado")
	public String getListAccountGIS(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountGIS(paramMap);
	}
	
	/**
	 * 수용가 : 목록 조회
	 */
	@JsonRequestMapping(value = "/account/getListAccount.ado")
	public String getListAccount(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccount(paramMap);
	}
	
	/**
	 * 수용가 : 수용가 엑셀
	 */
	@RequestMapping(value = "/account/exportExcelAccount.do")
	public ModelAndView exportExcelAccount(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
		String fileName = "수용가목록_"+ DateUtil.getCurrentDate("yyyyMmddHHmmss");
		String columnArr = "accountNo|accountNm|companyCd|companyNm|connectDtm|statusDevice|statusDisplay|deviceSn|meterSn|caliberCd|mtDown|mtDownDtm|mtLastDtm|fullAddr";
		String columnNmArr = "수용가 번호|수용가 이름|업체 코드|업체 이름|장비 최종접속시간|장비운영코드|단말기 상태정보|단말기 시리얼 번호|계량기 시리얼 번호|계량기 구경|계량기 동작 상태|계량기 동작시간|마지막 검침시간|전체 주소";
		String qId = "account.SELECT_LIST_ACCOUNT_EXCEL";
		
		paramMap.put("columnArr", columnArr);
		paramMap.put("columnNmArr", columnNmArr);
		paramMap.put("qId", qId);
		paramMap.put("fileName", fileName+".xlsx");
		
		return excelUtil.exportExcel(paramMap);
	}
	
	/**
	 * 기초구역별 수용가 cnt 조회
	 */
	@JsonRequestMapping(value = "/account/getListAccountCntInBaseArea.ado")
	public String getListAccountCntInBaseArea(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountCntInBaseArea(paramMap);
	}
	
	/**
	 * 수용가통계 수용가 사용량조회 
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataStats.ado")
	public String getListAccountDataStats(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataStats(paramMap);
	}
	
	/**
	 * ################
	 * 수용가 그룹
	 * ################
	 */
	
	/**
	 * 조회/관리 수용가 페이지
	 */
	@RequestMapping(value = {"/account/accountGroupList.do", "/{sub}/account/accountGroupList.do"})
	public String accountGroupList(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "sub", required = false) String sub) throws Exception {
		return "account/accountGroupList";
	}
	
	/**
	 * 수용가 그룹 : 목록 조회
	 */
	@JsonRequestMapping(value="/account/getListAccountGroup.ado")
	public String getListAccountGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountGroup(paramMap);
	}
	
	/**
	 * 수용가 그룹: 그룹 소속 수용가 조회
	 */
	@JsonRequestMapping(value = "/account/getListAccountInGroup.ado")
	public String getListAccountInGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountInGroup(paramMap);
	}
	
	/**
	 * 수용가 그룹: 수용가 등록
	 */
	@JsonRequestMapping(value = "/account/addAccountGroup.ado", method = RequestMethod.PUT)
	public String addAccountGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.insertAccountGroup(paramMap);
	}
	
	/**
	 * 수용가 그룹: 수용가 수정
	 */
	@JsonRequestMapping(value = "/account/modAccountGroup.ado", method = RequestMethod.PATCH)
	public String modAccountGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.updateAccountGroup(paramMap);
	}
	
	/**
	 * 수용가 그룹: 수용가 삭제
	 */
	@JsonRequestMapping(value = "/account/delAccountGroup.ado", method = RequestMethod.DELETE)
	public String delAccountGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.deleteAccountGroup(paramMap);
	}
	
	/**
	 * 수용가 그룹: 그룹 소속 수용가 체크 조회
	 */
	@JsonRequestMapping(value = "/account/getListAccountInGroupCheck.ado")
	public String getListAccountInGroupCheck(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountInGroupCheck(paramMap);
	}
	
	/**
	 * 수용가 그룹: 그룹 소속 수용가 추가
	 */
	@JsonRequestMapping(value = "/account/addAccountInGroup.ado", method=RequestMethod.PUT)
	public String addAccountInGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.insertAccountInGroup(paramMap);
	}
	
	/**
	 * 수용가 그룹: 그룹 소속 수용가 삭제
	 */
	@JsonRequestMapping(value = "/account/delAccountInGroup.ado", method=RequestMethod.DELETE)
	public String delAccountInGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.deleteAccountInGroup(paramMap);
	}
	
	/**
	 * ################
	 * 검침 데이터
	 * ################
	 */
	
	/**
	 * 조회/관리 검침 페이지
	 */
	@RequestMapping(value="/search/account/accountDataList.do")
	public String accountDataList(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return "account/accountDataList";
	}
	
	/**
	 * 검침 : 검침 데이터 목록 조회
	 */
	@JsonRequestMapping(value = "/account/getListAccountData.ado")
	public String getListAccountData(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountData(paramMap);
	}
	
	/**
	 * 검침 : 검침 데이터 엑셀
	 */
	@RequestMapping(value = "/account/exportExcelAccountData.do")
	public ModelAndView exportExcelAccountData(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
		System.out.println("일로 들어옴 검침 데이터 엑셀");

		String fileName = "수용가목록_"+DateUtil.getCurrentDate("yyyyMmddHHmmss");
		String columnArr = "accountNo|meterDtm|value|digits|leakState|termBatt|mLowBatt|mLeak|mOverload|mReverse|mNotUse";
		String columnNmArr = "수용가 번호|검침일시|지침값|소수점|누수예상|배터리 레벨|계량기 저전압|계량기 누수|계량기 과부하|계량기 역류|계량기 장기 미사용";
		String qId = "account.SELECT_LIST_ACCOUNT_DATA_EXCEL";
		
		paramMap.put("columnArr", columnArr);
		paramMap.put("columnNmArr", columnNmArr);
		paramMap.put("qId", qId);
		paramMap.put("fileName", fileName+".xlsx");
		
		return excelUtil.exportExcel(paramMap);
	}
	
	/**
	 * 검침 데이터 - 장비 이상 이벤트 조회 (차트)
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataForDeviceEventChart.ado")
	public String getListAccountDataForDeviceEventChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataForDeviceEventChart(paramMap);
	}
	
	/**
	 * 검침 데이터 - 시간별 증가량 조회 (차트)
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataForTimediffChart.ado")
	public String getListAccountDataForTimediffChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataForTimediffChart(paramMap);
	}
	
	/**
	 * 검침 데이터 - 월별 증가량 조회 (차트)
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataForMonthdiffChart.ado")
	public String getListAccountDataForMonthdiffChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataForMonthdiffChart(paramMap);
	}
	
	/**
	 * 검침 데이터 - 일별 증가량 조회 (차트)
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataForDaydiffChart.ado")
	public String getListAccountDataForDaydiffChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataForDaydiffChart(paramMap);
	}
	
	/**
	 * 검침 데이터 - 최대, 최소 수용가
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataMinMax.ado")
	public String getListAccountDataMinMax(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataMinMax(paramMap);
	}
	
	/**
	 * 검침 데이터 - 총 사용량, 평균 사용량
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataSumAvg.ado")
	public String getListAccountDataSumAvg(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataSumAvg(paramMap);
	}
	
	/**
	 * 검침 데이터 - 수도 총 사용량 TOP
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataTop.ado")
	public String getListAccountDataTop(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataTop(paramMap);
	}
	
	/**
	 * 검침 데이터 - 날짜별 평균 증가 - 라인차트
	 */
	@JsonRequestMapping(value = "/account/getListAccountDataStatsChart.ado")
	public String getListAccountDataStatsChart(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return accountService.selectListAccountDataStatsChart(paramMap);
	}
}