//package com.danusys.web.platform.config.security;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//@Slf4j
//@Service
//public class LoginDetailsService implements UserDetailsService {
//
////	@Autowired
////    SqlSessionTemplate sqlSession;
//
//	@Override
//	public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
////		LoginVO user = sqlSession.selectOne("selectUserById", id);
//
////		log.trace("# Login User {}", user.toString());
//
////		if(user==null) {
////			log.error("UsernameNotFoundException");
////            throw new UsernameNotFoundException(id);
////        }
//        return null;//user;
//	}
//
//}
