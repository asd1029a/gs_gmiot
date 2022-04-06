/*
package com.danusys.web.smartmetering.common.dao;

import java.util.List;
import java.util.Map;

import com.danusys.web.smartmetering.common.util.SessionUtil;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

@Mapper
public abstract class BaseDao {
	SqlSessionTemplate sqlSession;
    SqlSessionFactory sqlSessionFactory;

    public abstract void setSqlSessionTemplate(SqlSessionTemplate sqlSession);
    public abstract void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory);
    
	public List<Map<String, Object>> selectList(String qid, Map<String, Object> paramMap) throws Exception {
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
		return sqlSession.selectOne(qid, paramObject);
	}
	
	public int insert(String qid, Map<String, Object> paramMap) throws Exception {
		paramMap.put("sessionAdminSeq", SessionUtil.getSessionAdminSeq());
		return sqlSession.insert(qid, paramMap);
	}
	
	public int update(String qid, Map<String, Object> paramMap) throws Exception {
		paramMap.put("sessionAdminSeq", SessionUtil.getSessionAdminSeq());
		return sqlSession.update(qid, paramMap);
	}
	
	public int delete(String qid, Map<String, Object> paramMap) throws Exception {
		return sqlSession.delete(qid, paramMap);
	}
}*/
