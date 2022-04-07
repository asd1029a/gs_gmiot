package com.danusys.web.commons.auth.session.util;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

import java.util.List;
import java.util.Map;

@Mapper
public abstract class BaseDao {
	SqlSessionTemplate sqlSession;
    SqlSessionFactory sqlSessionFactory;

    public abstract void setSqlSessionTemplate(SqlSessionTemplate sqlSession);
    public abstract void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);
    
	public List<Map<String, Object>> selectList(String qid, Map<String, Object> paramMap) throws Exception {
		System.out.println("?????????????" + qid);
		System.out.println("?????????????" + paramMap.values());



		return sqlSession.selectList(qid, paramMap);
	}
	
	public <E> List<E> selectListObject(String qid, Map<String, Object> paramMap) throws Exception {
		return sqlSession.selectList(qid, paramMap);
	}
	
	public Map<String, Object> selectOne(String qid, Map<String, Object> paramMap) throws Exception {
		paramMap.put("sessionAdminSeq", SessionUtil.getSessionAdminSeq());
		return sqlSession.selectOne(qid, paramMap);
	}
	
	public <T> T selectOneObject(String qid, Object paramObject) throws Exception {
		System.out.println("paramobject값 :  " + paramObject);

		return sqlSession.selectOne(qid, paramObject);
	}
	
	public int insert(String qid, Map<String, Object> paramMap) throws Exception {
		paramMap.put("sessionAdminSeq", SessionUtil.getSessionAdminSeq());
		return sqlSession.insert(qid, paramMap);
	}
	
	public int update(String qid, Map<String, Object> paramMap) throws Exception {
		paramMap.put("sessionAdminSeq", SessionUtil.getSessionAdminSeq());
		System.out.println("sessionAdminSeq : " + SessionUtil.getSessionAdminSeq());
		System.out.println("qid , parammap : " + qid + "/ " + paramMap.values());
		return sqlSession.update(qid, paramMap);
	}
	
	public int delete(String qid, Map<String, Object> paramMap) throws Exception {
		return sqlSession.delete(qid, paramMap);
	}
}