package com.danusys.web.platform.service.login;

import com.danusys.web.platform.common.util.EgovFileScrty;
import com.danusys.web.platform.model.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class LoginServiceImpl implements LoginService {
	
	@Value("${danusys.salt.text}")
	private String saltText = "";

	@Value("${danusys.db.encrypt}")
	private String dbEncrypt = "";

	@Autowired
    SqlSessionTemplate sqlSession;
    
	public LoginVO login(HttpServletRequest request, String id, String pwd) throws Exception {
		return this.loginSel(request, id, pwd);
	}
	
	private LoginVO loginSel(HttpServletRequest request, String id, String pwd) throws Exception {
		/* 로그기록 */
		InetAddress local = null;
		try {
			local = InetAddress.getLocalHost();
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		LoginVO loginVo = null;
		Map<String,Object> param = new HashMap<String,Object>();
		Map<String,Object> userInfo = new HashMap<String,Object>();
//	    String saltText = thithis.propertiesService.getString("Globals.SaltText").trim();
//	    String dbEncryptTag = this.propertiesService.getString("Globals.DBEncrypt").trim();
	    String userPw;
		String ip = local.getHostAddress();

		userPw = EgovFileScrty.encryptPassword(pwd, this.saltText);
	    
		param.put("id", id);
		param.put("pwd", userPw);
		
		
		loginVo = sqlSession.selectOne("admin.selectLogin", param);
		log.trace("============    [1]   ============ : {}", ToStringBuilder.reflectionToString(loginVo));
		
		return loginVo;
	}
	
	
}
