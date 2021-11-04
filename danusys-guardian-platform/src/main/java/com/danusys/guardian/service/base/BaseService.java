package com.danusys.guardian.service.base;

import org.json.JSONArray;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/*
//  인터페이스  명 : BaseService (기본 Service)
//  인터페이스설명 : BaseService (기본 Service) 인터페이스
*/
public interface BaseService
{
    // List 조회
    public <T> List<T> baseSelectList(String sqlid, Map<String, Object> param);

    // 단건 조회
    public String baseSelectOne(String sqlid, Map<String, Object> param);

    public Object baseSelectOneObject(String sqlid, Map<String, Object> param);

    // 등록 (Insert Key 반환)
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public String baseInsertReturnSelectKey(String sqlid, Map<String, Object> param) throws Exception;

    // 등록
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int baseInsert(String sqlid, Map<String, Object> param) throws Exception;

    // 수정
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int baseUpdate(String sqlid, Map<String, Object> param) throws Exception;

    // 삭제
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int baseDelete(String sqlid, Map<String, Object> param) throws Exception;

    // Ajax 등록 (Insert Key 반환) C/U/D 처리
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public String ajaxInsertKey(String sqlid, Map<String, Object> param, JSONArray jsonArray2) throws Exception;
    
    // Ajax C/U/D 처리
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int ajaxSave(String sqlCid, String sqlUid, String sqlDid, JSONArray jsonArray) throws Exception;
    
    // Insert Key Transaction C/U/D N건 처리
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int insertKeyMultiTransaction(String sqlid1, String sqlid2, String sqlid3, String sqlid4, JSONArray jsonArray1, JSONArray jsonArray2, JSONArray jsonArray3, JSONArray jsonArray4) throws Exception;

    // Transaction C/U/D N건 처리
    @Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public int setMultiTransaction(String sqlid1, String sqlid2, String sqlid3, String sqlid4, JSONArray jsonArray1, JSONArray jsonArray2, JSONArray jsonArray3, JSONArray jsonArray4) throws Exception;
  
}
