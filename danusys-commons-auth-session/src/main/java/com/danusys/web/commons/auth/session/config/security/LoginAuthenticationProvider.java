/*

package com.danusys.web.commons.auth.config.security;

import com.danusys.web.commons.app.EgovFileScrty;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetails;
import com.danusys.web.commons.auth.config.auth.CommonsUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
public class LoginAuthenticationProvider implements AuthenticationProvider {

	@Value("${danusys.salt.text}")
	private String saltText = "";

	@Autowired
	private CommonsUserDetailsService commonsUserDetailsService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		log.info("여기 들어옴 AuthenticationProvider");

		String id = authentication.getName();
		String pwd = authentication.getCredentials().toString();
		String encryptPwd = null;

		log.info("id : " +id +" / " +pwd);

		try {
			encryptPwd = EgovFileScrty.encryptPassword(pwd, this.saltText);
			log.trace("# encryptPwd : {}", encryptPwd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get user data for database
	//	LoginVO loginVO = (LoginVO) loginDetailService.loadUserByUsername(id);
		CommonsUserDetails user = commonsUserDetailsService.loadUserByUsername(id);

		log.trace("user.getPassword() : {}", user.getPassword());


		if (user == null || !id.equals(user.getUserId())) {// || !encryptPwd.equals(user.getPassword())) {
			log.error("No ID or wrong password");
			throw new BadCredentialsException(id);
		} else if (!user.isAccountNonLocked()) {
			log.error("isAccountNonLocked");
			throw new LockedException(id);
		} else if (!user.isEnabled()) {
			log.error("isEnabled");
			throw new DisabledException(id);
		} else if (!user.isAccountNonExpired()) {
			log.error("isAccountNonExpired");
			throw new AccountExpiredException(id);
		} else if (!user.isCredentialsNonExpired()) {
			log.error("isCredentialsNonExpired");
			throw new CredentialsExpiredException(id);
		}

		user.setPassword();

		//Authentication result = new UsernamePasswordAuthenticationToken(id, encryptPwd, user.getAuthorities());

		Authentication result = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(id, encryptPwd));
		SecurityContextHolder.getContext().setAuthentication(result);

		log.info("result:: : " + result);

		Authentication result2 = SecurityContextHolder.getContext().getAuthentication();
		if (result2.getPrincipal() instanceof CommonsUserDetails) {
			CommonsUserDetails commonsUserDetails = (CommonsUserDetails) result2.getPrincipal();
			log.info("### id, username : {}, {}", commonsUserDetails.getUserId(), commonsUserDetails.getUsername());
		}

		return result;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}

*/
