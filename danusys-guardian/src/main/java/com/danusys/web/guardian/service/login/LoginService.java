package com.danusys.web.guardian.service.login;

import com.danusys.web.guardian.model.LoginVO;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {
	public LoginVO login(HttpServletRequest request, String id, String pwd) throws Exception ;
	
	
}
