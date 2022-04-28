package com.danusys.web.smartmetering.admin.service;

import java.util.Map;

public interface AdminService {
	
/*
	 * ################
	 * 관리자
	 * ################ 
	*/

	public String selectListAdmin(Map<String, Object> paramMap) throws Exception;
	//public AdminInfo selectDetailAdminLogin(Map<String, Object> paramMap) throws Exception;
	public Object selectDetailAdmin(Object paramObject) throws Exception;
	public String checkDuplAdminId(Object paramObject) throws Exception;
	public String insertAdmin(Map<String, Object> paramMap) throws Exception;
	public String updateAdmin(Map<String, Object> paramMap) throws Exception;
	public String deleteAdmin(Map<String, Object> paramMap) throws Exception;
	public String updateAdminPwd(Map<String, Object> paramMap) throws Exception;
	public String insertAdminLoginLog(Map<String, Object> paramMap) throws Exception;
	public String updateAdminAfterLogin(Map<String, Object> paramMap) throws Exception;
	
/*
	 * ################
	 * 관리자 : 그룹
	 * ################
*/

	public String selectListAdminGroup(Map<String, Object> paramMap) throws Exception;
	public String selectListAdminInGroup(Map<String, Object> paramMap) throws Exception;
	public String selectDetailAdminGroup(Map<String, Object> paramMap) throws Exception;
	public String insertAdminGroup(Map<String, Object> paramMap) throws Exception;
	public String updateAdminGroup(Map<String, Object> paramMap) throws Exception;
	public String deleteAdminGroup(Map<String, Object> paramMap) throws Exception;
	public String insertAdminInGroup(Map<String, Object> paramMap) throws Exception;
	public String deleteAdminInGroup(Map<String, Object> paramMap) throws Exception;
	public String selectListPermit(Map<String, Object> paramMap) throws Exception;
	public String selectListAdminInGroupCheck(Map<String, Object> paramMap) throws Exception;
}
