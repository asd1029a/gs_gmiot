package com.danusys.guardian.service.login;

import com.danusys.guardian.model.LoginVO;

import javax.servlet.http.HttpServletRequest;

public interface LoginService {
	public LoginVO login(HttpServletRequest request, String id, String pwd) throws Exception ;
	
	
}
