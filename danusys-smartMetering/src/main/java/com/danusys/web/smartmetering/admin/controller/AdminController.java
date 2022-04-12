package com.danusys.web.smartmetering.admin.controller;

import com.danusys.web.commons.auth.session.service.AdminService;
import com.danusys.web.smartmetering.common.annotation.JsonRequestMapping;
import com.danusys.web.smartmetering.common.util.DateUtil;
import com.danusys.web.smartmetering.common.util.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminController {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	private final AdminService adminService;


	@Autowired
	ExcelUtil excelUtil;

	/**
	 * ################
	 * 관리자
	 * ################
	 */

	/**
	 * 관리자 : 로그인 페이지
	 */
	@RequestMapping(value="/login")
	public String loginForm(HttpServletRequest request, HttpServletResponse response) throws Exception {

		return "admin/loginForm";
	}


	/**
	 * 관리자 : 사용자 계정 페이지
	 */
	@RequestMapping(value = {"/admin/adminList.do", "/{sub}/admin/adminList.do"})
	public String adminList(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "sub", required = false) String sub) throws Exception {
		return "admin/adminList";
	}

	/**
	 * 관리자 : 사용자 목록 조회
	 */
	@JsonRequestMapping(value = "/admin/getListAdmin.ado")
	public String getListAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.selectListAdmin(paramMap);
	}

	/**
	 * 관리자 : 사용자 등록
	 */
	@JsonRequestMapping(value = "/admin/addAdmin.ado", method = RequestMethod.PUT)
	public String addAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.insertAdmin(paramMap);
	}

	/**
	 * 관리자 : 사용자 아이디 중복조회
	 */
	@JsonRequestMapping(value = "/admin/checkDuplAdminId.ado")
	public String checkDuplAdminId(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.checkDuplAdminId(paramMap);
	}

	/**
	 * 관리자 : 사용자 수정
	 */
	@JsonRequestMapping(value = "/admin/modAdmin.ado", method = RequestMethod.PATCH)
	public String modAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.updateAdmin(paramMap);
	}

	/**
	 * 관리자 : 사용자 삭제
	 */
	@JsonRequestMapping(value = "/admin/delAdmin.ado", method = RequestMethod.DELETE)
	public String delAdmin(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.deleteAdmin(paramMap);
	}

	/**
	 * 관리자 : 사용자 비밀번호 수정
	 */
	@JsonRequestMapping(value = "/admin/modAdminPwd.ado", method = RequestMethod.PATCH)
	public String modAdminPwd(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.updateAdminPwd(paramMap);
	}

	/**
	 * 관리자 : 사용자 엑셀
	 */
	@RequestMapping(value = "/admin/exportExcelAdmin.do")
	public ModelAndView exportAdmin(HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> paramMap) throws Exception {
		String fileName = "사용자목록_"+DateUtil.getCurrentDate("yyyyMmddHHmmss");
		String columnArr = "id|tel|address|detailAddress|employeeNumber|codeName";
		String columnNmArr = "관리자 ID|전화번호|주소|상세주소|사번|상태";
		String qId = "admin.SELECT_LIST_ADMIN_EXCEL";

		paramMap.put("columnArr", columnArr);
		paramMap.put("columnNmArr", columnNmArr);
		paramMap.put("qId", qId);
		paramMap.put("fileName", fileName+".xlsx");



		return excelUtil.exportExcel(paramMap);

	}

	/**
	 * ################
	 * 관리자 그룹
	 * ################
	 */

	/**
	 * 관리자 그룹 : 사용자 그룹 페이지
	 */
	@RequestMapping(value = {"/admin/adminGroupList.do", "/{sub}/admin/adminGroupList.do"})
	public String adminGroupList(HttpServletRequest request, HttpServletResponse response, @PathVariable(name = "sub", required = false) String sub) throws Exception {
		return "admin/adminGroupList";
	}

	/**
	 * 관리자 그룹: 사용자 그룹 조회
	 */
	@JsonRequestMapping(value = "/admin/getListAdminGroup.ado")
	public String getListAdminGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.selectListAdminGroup(paramMap);
	}

	/**
	 * 관리자 그룹: 그룹 소속 관리자 조회
	 */
	@JsonRequestMapping(value = "/admin/getListAdminInGroup.ado")
	public String getListAdminInGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {

		logger.info("### getListAdminInGroup {} ", paramMap.toString());

//		List columns  = (List)paramMap.get("columns");
//		Map row1 = (Map) columns.get(0);
//		row1.put("data", "adminId");
//
//		Map row2 = (Map) columns.get(1);
//		row2.put("data", "adminName");


		return adminService.selectListAdminInGroup(paramMap);
	}

	/**
	 * 관리자 그룹: 그룹 권한 조회
	 */
	@JsonRequestMapping(value = "/admin/getListPermit.ado")
	public String getListPermit(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.selectListPermit(paramMap);
	}

	/**
	 * 관리자 그룹: 사용자 등록
	 */
	@JsonRequestMapping(value = "/admin/addAdminGroup.ado", method = RequestMethod.PUT)
	public String addAdminGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.insertAdminGroup(paramMap);
	}

	/**
	 * 관리자 그룹: 사용자 수정
	 */
	@JsonRequestMapping(value = "/admin/modAdminGroup.ado", method = RequestMethod.PATCH)
	public String modAdminGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		System.out.println("관리자그룹 컨트롤러 사용자 수정 : " + paramMap);
		return adminService.updateAdminGroup(paramMap);
	}

	/**
	 * 관리자 그룹: 사용자 삭제
	 */
	@JsonRequestMapping(value = "/admin/delAdminGroup.ado", method = RequestMethod.DELETE)
	public String delAdminGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.deleteAdminGroup(paramMap);
	}

	/**
	 * 관리자 그룹: 그룹 소속 관리자 체크 조회
	 */
	@JsonRequestMapping(value = "/admin/getListAdminInGroupCheck.ado")
	public String getListAdminInGroupCheck(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.selectListAdminInGroupCheck(paramMap);
	}

	/**
	 * 관리자 그룹: 그룹 소속 관리자 추가
	 */
	@JsonRequestMapping(value = "/admin/addAdminInGroup.ado", method=RequestMethod.PUT)
	public String addAdminInGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.insertAdminInGroup(paramMap);
	}

	/**
	 * 관리자 그룹: 그룹 소속 관리자 삭제
	 */
	@JsonRequestMapping(value = "/admin/delAdminInGroup.ado", method=RequestMethod.DELETE)
	public String delAdminInGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> paramMap) throws Exception {
		return adminService.deleteAdminInGroup(paramMap);
	}
}