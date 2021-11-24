package com.danusys.web.platform.model;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


/*
//  클래스  명 : BaseDaoImpl (기본 Dao)
//  클래스설명 : BaseDao (기본 Dao) 인터페이스 구현 클래스
*/
@Repository
public class BaseDaoImpl implements BaseDao 
{
//    private static final Logger logger = LoggerFactory.getLogger(BaseDaoImpl.class);


//    @Resource(name = "sqlSessionTemplate")
    @Autowired
    SqlSessionTemplate sqlSession;
    
    /**
     * FuncName : baseSelectList()
     * FuncDesc : List 조회
     * Param    : sqlid : SQL ID,
     *          : param : SQL Parameter
     * Return   : <T> List<T>
    */
    @Override
    public <T> List<T> baseSelectList(String sqlid, Map<String, Object> param) {
    		return sqlSession.selectList(sqlid, param);
    }


    /**
     * FuncName : baseSelectOne()
     * FuncDesc : 단일 String 조회
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : String
    */
    @Override
    public String baseSelectStringOne(String sqlid, Map<String, Object> param)
    {
        return sqlSession.selectOne(sqlid, param);
    }

    /**
     * 단건 제네릭 타입 Object 리턴
     * @param sqlid
     * @param param
     * @return
     */
    @Override
    public Object baseSelectObjectOne(String sqlid, Map<String, Object> param) {
        return sqlSession.selectOne(sqlid, param);
    }

    /**
     * FuncName : baseSelectOne()
     * FuncDesc : 단일 Map 조회
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : String
    */
    @Override
    public Map<String,Object> baseSelectMapOne(String sqlid, Map<String, Object> param)
    {
        return sqlSession.selectOne(sqlid, param);
    }


    /**
     * FuncName : baseInsertReturnSelectKey()
     * FuncDesc : 등록 (Insert Key 반환)
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : String (Insert Key 반환) 
    */
    @Override
    public String baseInsertReturnSelectKey(String sqlid, Map<String, Object> param)
    {
    	sqlSession.insert(sqlid, param);
        return param.get("idKey").toString();
    }


    /**
     * FuncName : baseInsert()
     * FuncDesc : 등록
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : int
    */
    @Override
    public int baseInsert(String sqlid, Map<String, Object> param)
    {
        return sqlSession.insert(sqlid, param);
    }

    /**
     * FuncName : baseUpdate()
     * FuncDesc : 수정
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : int
    */
    @Override
    public int baseUpdate(String sqlid, Map<String, Object> param)
    {
        return sqlSession.update(sqlid, param);
    }


    /**
     * FuncName : baseDelete()
     * FuncDesc : 삭제
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : int
    */
    @Override
    public int baseDelete(String sqlid, Map<String, Object> param)
    {
        return sqlSession.delete(sqlid, param);
    }


}
