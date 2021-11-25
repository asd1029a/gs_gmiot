package com.danusys.web.platform.config.security;

import com.danusys.web.commons.util.EgovFileScrty;
import com.danusys.web.platform.model.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

@Slf4j
public class LoginAuthenticationProvider implements AuthenticationProvider {

	@Value("${danusys.salt.text}")
	private String saltText = "";

	@Autowired
	private LoginDetailsService loginDetailService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String id = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		String encryptPwd = null;

		try {
			encryptPwd = EgovFileScrty.encryptPassword(pwd, this.saltText);
			log.trace("# encryptPwd : {}", encryptPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get user data for database
		LoginVO loginVO = (LoginVO) loginDetailService.loadUserByUsername(id);

		if (loginVO == null || !id.equals(loginVO.getId()) || !encryptPwd.equals(loginVO.getPwd())) {
			log.error("No ID or wrong password");
			throw new BadCredentialsException(id);
		} else if (!loginVO.isAccountNonLocked()) {
			log.error("isAccountNonLocked");
			throw new LockedException(id);
		} else if (!loginVO.isEnabled()) {
			log.error("isEnabled");
			throw new DisabledException(id);
		} else if (!loginVO.isAccountNonExpired()) {
			log.error("isAccountNonExpired");
			throw new AccountExpiredException(id);
		} else if (!loginVO.isCredentialsNonExpired()) {
			log.error("isCredentialsNonExpired");
			throw new CredentialsExpiredException(id);
		}

		loginVO.setPwd(null);

		Authentication result = new UsernamePasswordAuthenticationToken(id, null, loginVO.getAuthorities());

		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
