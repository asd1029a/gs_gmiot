package com.danusys.web.smartmetering.admin.service.impl;

import com.danusys.web.smartmetering.admin.service.AdminService;
import com.danusys.web.smartmetering.common.dao.CommonDao;
import com.danusys.web.smartmetering.common.util.JsonUtil;
import com.danusys.web.smartmetering.common.util.PagingUtil;
import com.danusys.web.smartmetering.common.util.SessionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

	//@Autowired
	//AuthenticationManager authenticationManager;
	//@Autowired
	//SecurityContextRepository repository;
	@Autowired
	CommonDao commonDao;
	//@Autowired
	//PasswordEncoder passwordEncoder;
	@Autowired
	PagingUtil pagingUtil;

	@Autowired
	PasswordEncoder passwordEncoder;

	SessionUtil sessionUtil;

	/**
	 * ################
	 * 관리자
	 * ################
	 */

	/**
	 * 관리자 : 목록 조회
	 */
	@Override
	public String selectListAdmin(Map<String, Object> paramMap) throws Exception {

		String result = JsonUtil.getOriJsonString(pagingUtil.getSettingMap("admin.SELECT_LIST_ADMIN", paramMap));
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("admin.SELECT_LIST_ADMIN", paramMap));
	}

	/**
	 * 관리자 : 상세 조회(로그인)
	 */
	/*@Override
	public AdminInfo selectDetailAdminLogin(Map<String, Object> paramMap) throws Exception {
		return (AdminInfo) commonDao.selectOneObject("admin.SELECT_DETAIL_ADMIN_LOGIN", paramMap);
	}*/

	/**
	 * 관리자 : 상세 조회
	 */
	@Override
	public Object selectDetailAdmin(Object paramObject) throws Exception {
		System.out.println("adminserviceimple에 selectDetailAdmin");
		Object resultObject = null;
		Object returnObject = null;

		resultObject = commonDao.selectOneObject("SELECT_DETAIL_ADMIN", paramObject);

		System.out.println("####타입 " +  resultObject.getClass().getName());

		return resultObject;
	}

	/**
	 * 관리자 : 아이디 중복 확인
	 */
	@Override
	public String checkDuplAdminId(Object paramObject) throws Exception {
		Object resultObject = null;
		resultObject = commonDao.selectOneObject("admin.SELECT_CHECK_DUPL_ADMIN_ID", paramObject);

		return JsonUtil.getJsonString(resultObject);
	};

	/**
	 * 관리자 : 등록
	 */
	@Override
	public String insertAdmin(Map<String, Object> paramMap) throws Exception {
		paramMap.put("password", passwordEncoder.encode(paramMap.get("password").toString()));
		return JsonUtil.getCntJsonString(commonDao.insert("admin.INSERT_ADMIN", paramMap));

	}

	/**
	 * 관리자 : 수정
	 */
	@Override
	public String updateAdmin(Map<String, Object> paramMap) throws Exception {
		log.info("관리자 수정 : " + paramMap);
		if(paramMap.get("password") != null) {
			paramMap.put("password", passwordEncoder.encode(paramMap.get("password").toString()));
		}
		log.info("관리자 수정 파람 패스워드 : " + paramMap.get("password"));
		return 	JsonUtil.getCntJsonString(commonDao.insert("admin.UPDATE_ADMIN", paramMap));
	}

	/**
	 * 관리자 : 패스워드 변경
	 */
	@Override
	public String updateAdminPwd(Map<String, Object> paramMap) throws Exception {
		paramMap.put("password", passwordEncoder.encode(paramMap.get("password").toString()));
		return JsonUtil.getCntJsonString(commonDao.insert("admin.UPDATE_ADMIN_PASSWORD", paramMap));

	}

	/**
	 * 관리자 : 삭제
	 */
	@Override
	public String deleteAdmin(Map<String, Object> paramMap) throws Exception {
		commonDao.delete("admin.DELETE_ADMIN_IN_ADMIN_GROUP_ADMIN_SEQ", paramMap);
		return JsonUtil.getCntJsonString(commonDao.delete("admin.DELETE_ADMIN", paramMap));
	}

	/**
	 * 관리자 : 로그인 이력 등록
	 */
	@Override
	public String insertAdminLoginLog(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.update("admin.INSERT_ADMIN_LOGIN_LOG", paramMap));
	}

	/**
	 * 관리자 : 로그인 이후 업데이트
	 */
	public String updateAdminAfterLogin(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.update("admin.UPDATE_ADMIN_AFTER_LOGIN", paramMap));
	}


	/**
	 * ################
	 * 관리자 그룹
	 * ################
	 */

	/**
	 * 관리자 그룹 : 목록 조회
	 */
	@Override
	public String selectListAdminGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("admin.SELECT_LIST_ADMIN_GROUP", paramMap));
	}

	/**
	 * 관리자 그룹 : 소속 관리자 목록 조회
	 */
	@Override
	public String selectListAdminInGroup(Map<String, Object> paramMap) throws Exception {
		log.info("####selectListAdminInGroup : {} ",paramMap.toString());

		return JsonUtil.getOriJsonString(pagingUtil.getSettingMap("admin.SELECT_LIST_ADMIN_IN_GROUP", paramMap));
	}

	/**
	 * 관리자 그룹 : 상세 조회
	 */
	@Override
	public String selectDetailAdminGroup(Map<String, Object> paramMap) throws Exception {

		return JsonUtil.getJsonString(commonDao.selectOne("admin.SELECT_DETAIL_ADMIN_GROUP", paramMap));
	}

	/**
	 * 관리자 그룹 : 등록
	 */
	@Override
	public String insertAdminGroup(Map<String, Object> paramMap) throws Exception {

		commonDao.insert("admin.INSERT_ADMIN_GROUP", paramMap);

		List<?> permitSeqList = new ArrayList<String>();
		permitSeqList = (List<?>) paramMap.get("permitSeqList");  // seqlist가 들어와야한다.

		paramMap.put("permitSeq", 33 );  // 임시

		if(!permitSeqList.isEmpty()) {
			commonDao.insert("admin.INSERT_ADMIN_GROUP_PERMIT", paramMap);
		}

		System.out.println("관리자 그룹 등록 파람맵 : " + paramMap);

		return JsonUtil.getCntJsonString(1);
	}

	/**
	 * 관리자 그룹 : 수정
	 */
	@Override
	public String updateAdminGroup(Map<String, Object> paramMap) throws Exception {
		List<?> permitSeqList = new ArrayList<String>();
		permitSeqList = (List<?>) paramMap.get("permitSeqList");

		paramMap.put("permitSeq", 33);

		commonDao.delete("admin.DELETE_ADMIN_GROUP_PERMIT", paramMap);
		if(!permitSeqList.isEmpty()) {
			commonDao.insert("admin.INSERT_ADMIN_GROUP_PERMIT", paramMap);
		}
		return JsonUtil.getCntJsonString(commonDao.update("admin.UPDATE_ADMIN_GROUP", paramMap));
	}

	/**
	 * 관리자 그룹 : 삭제
	 */
	@Override
	public String deleteAdminGroup(Map<String, Object> paramMap) throws Exception {

		commonDao.delete("admin.DELETE_ADMIN_GROUP_PERMIT", paramMap);
		commonDao.delete("admin.DELETE_ADMIN_IN_ADMIN_GROUP_GROUP_SEQ", paramMap);
		return JsonUtil.getCntJsonString(commonDao.delete("admin.DELETE_ADMIN_GROUP", paramMap));
	}

	/**
	 * 관리자 그룹 : 소속 관리자 등록
	 */
	@Override
	public String insertAdminInGroup(Map<String, Object> paramMap) throws Exception {
		commonDao.delete("admin.DELETE_ADMIN_IN_ADMIN_GROUP_GROUP_SEQ", paramMap);
		return JsonUtil.getCntJsonString(commonDao.insert("admin.INSERT_ADMIN_IN_ADMIN_GROUP", paramMap));
	}

	/**
	 * 관리자 그룹 : 소속 관리자 해제
	 */
	@Override
	public String deleteAdminInGroup(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getCntJsonString(commonDao.delete("admin.DELETE_ADMIN_IN_ADMIN_GROUP_ADMIN_SEQ", paramMap));
	}

	/**
	 * 관리자 그룹 : 소속관리자 체크 조회
	 */
	@Override
	public String selectListAdminInGroupCheck(Map<String, Object> paramMap) throws Exception {

		return JsonUtil.getJsonString(commonDao.selectList("admin.SELECT_LIST_ADMIN_IN_GROUP_CHECK", paramMap));
	}

	/**
	 * 관리자 그룹 : 권한 목록 조회
	 */
	@Override
	public String selectListPermit(Map<String, Object> paramMap) throws Exception {
		return JsonUtil.getJsonString(commonDao.selectList("admin.SELECT_LIST_PERMIT", paramMap));
	}
}

