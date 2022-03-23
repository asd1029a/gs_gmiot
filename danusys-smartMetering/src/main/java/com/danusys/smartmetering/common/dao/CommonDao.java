package com.danusys.smartmetering.common.dao;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
//@Mapper
public class CommonDao extends BaseDao {
	
	@Override
	@Resource(name="sqlSessionTemplate")
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSession) {
		super.sqlSession = sqlSession;
	}

	@Override
	@Resource(name="sqlSessionFactory")
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		super.sqlSessionFactory = sqlSessionFactory;
	}
}