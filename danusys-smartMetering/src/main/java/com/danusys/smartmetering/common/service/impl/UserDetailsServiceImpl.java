/*package com.danusys.smartmetering.common.service.impl;

import com.danusys.smartmetering.admin.service.AdminService;
import com.danusys.smartmetering.common.model.AdminInfo;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	@Resource(name="sqlSessionTemplate")
	SqlSessionTemplate sqlSession;
	@Autowired
	AdminService adminService;
	
	public UserDetails loadUserByUsername(String id) {
		AdminInfo adminInfo = null;
		Map<String, Object> paramMap = new HashMap<String, Object>();
		
		paramMap.put("adminId", id);
		adminInfo = sqlSession.selectOne("admin.SELECT_DETAIL_ADMIN_LOGIN", paramMap);
		
		if(adminInfo==null) {
			throw new UsernameNotFoundException("해당 아이디가 존재하지 않습니다");
		}
		return adminInfo;
	}
}*/