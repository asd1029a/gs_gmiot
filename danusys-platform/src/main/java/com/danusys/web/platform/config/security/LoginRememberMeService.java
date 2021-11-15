package com.danusys.web.platform.config.security;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.AbstractRememberMeServices;
import org.springframework.security.web.authentication.rememberme.CookieTheftException;
import org.springframework.security.web.authentication.rememberme.RememberMeAuthenticationException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginRememberMeService extends AbstractRememberMeServices {
	@Autowired
	private LoginDetailsService loginDetailService;
	
	@Resource(name = "sqlSessionTemplate")
	SqlSessionTemplate sqlSession;
	
	SecureRandom random;

	protected LoginRememberMeService(String key, UserDetailsService userDetailsService) {
		super(key, userDetailsService);
		random = new SecureRandom();
	}

	@Override
	protected void onLoginSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication successfulAuthentication) {
		String cookieValue = super.extractRememberMeCookie(request);
		
		String username = successfulAuthentication.getName();
		
		if(cookieValue != null) {
			sqlSession.delete("security.deleteToken", decodeCookie(cookieValue)[0]);
		}
		
		if(username != null) {
			sqlSession.delete("security.deleteTokenByUsername", username);
		}
		
		String series = generateTokenValue();
		String token = generateTokenValue();
		
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("username", username);
			param.put("series", series);
			param.put("token", token);
			param.put("lastUsed", new Date());
			
			sqlSession.insert("security.insertUserToken", param);
			
			String[] rawCookie = new String[] {series, token};
			super.setCookie(rawCookie, getTokenValiditySeconds(), request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request,
			HttpServletResponse response) throws RememberMeAuthenticationException, UsernameNotFoundException {
		
		if(cookieTokens.length != 2) {
			throw new RememberMeAuthenticationException("invalid cookie value");
		}
		
		String cookieSeries = cookieTokens[0];
		String cookieToken = cookieTokens[1];
		
		Map<String, Object> param = new HashMap<String, Object>();
		
		param.put("series", cookieSeries);
		param.put("token", cookieToken);
		
		Map<String, Object> rememberMeData = sqlSession.selectOne("security.selectUserByToken", param);
		
		if(rememberMeData == null) {
			throw new RememberMeAuthenticationException("not user cookie");
		}
		
		String username = rememberMeData.get("username").toString();
		
		if(!cookieToken.equals(rememberMeData.get("token"))) {
			sqlSession.delete("security.deleteToken", cookieSeries);
			
			throw new CookieTheftException("Tampered cookie");
		}
		
		Date lastUsed = (Date) rememberMeData.get("lastUsed");
		
		if(lastUsed.getTime() + getTokenValiditySeconds() * 1000L < System.currentTimeMillis()) {
			sqlSession.delete("security.deleteToken", cookieSeries);
			
			throw new RememberMeAuthenticationException("Expiration date cookie");
		}
		
		String newToken = generateTokenValue();
		rememberMeData.put("token", newToken);
		rememberMeData.put("lastUsed", new Date());
		
		try {
			sqlSession.update("security.updateUserToken", rememberMeData);
			
			String[] rawCookie = new String[] {cookieSeries, newToken};
			super.setCookie(rawCookie, getTokenValiditySeconds(), request, response);
		} catch(DataAccessException e) {
			e.printStackTrace();
			throw new RememberMeAuthenticationException("new token update fail");
		}
		
		/*HttpSession session = request.getSession();
		LoginVO loginVO = (LoginVO) session.getAttribute("admin");
		
		if(loginVO == null) {
			loginVO = (LoginVO) loginDetailService.loadUserByUsername(username);
			
			loginVO.setSessionId(session.getId());
			
			session.setAttribute("id", loginVO.getUsername());
			session.setAttribute("pwd", loginVO.getPassword());
			session.setAttribute("admin", loginVO);
		}*/
		
		return getUserDetailsService().loadUserByUsername(username);
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
		String decodedCookieVal = super.extractRememberMeCookie(request);
		
		if(decodedCookieVal != null) {
			String[] cookieTokens = super.decodeCookie(decodedCookieVal);
			
			if(cookieTokens != null && cookieTokens.length == 2) {
				sqlSession.delete("security.deleteToken", cookieTokens[0]);
			}
		}
		
		super.logout(request, response, authentication);
	}
	
	private String generateTokenValue() {
		byte[] token = new byte[16];
		random.nextBytes(token);
		return new String(Base64.encode(token));
	}

}
