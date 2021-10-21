package com.danusys.guardian.service.base;

import com.danusys.guardian.common.util.JsonUtil;
import com.danusys.guardian.model.BaseDao;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/*
//  클래스  명 : BaseServiceImpl (기본 Service)
//  클래스설명 : BaseServiceImpl (기본 Service) 인터페이스 구현 클래스
*/
@Service
public class BaseServiceImpl implements BaseService
{
    private static final Logger logger = LoggerFactory.getLogger(BaseServiceImpl.class);

    @Autowired
    private BaseDao baseDao;

    /**
     * FuncName : baseSelectList()
     * FuncDesc : List 조회
     * Param    : sqlid : SQL ID,
     *          : param : SQL Parameter
     * Return   : <T> List<T>
    */
    @Override
    public <T> List<T> baseSelectList(String sqlid, Map<String, Object> param)
    {
        return baseDao.baseSelectList(sqlid, param);
    }


    /**
     * FuncName : baseSelectOne()
     * FuncDesc : 단일 조회
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : String
    */
    @Override
    public String baseSelectOne(String sqlid, Map<String, Object> param)
    {
        return baseDao.baseSelectStringOne(sqlid, param);
    }


    /**
     * FuncName : baseInsertReturnSelectKey()
     * FuncDesc : 등록 (Insert Key 반환)
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : String (Insert Key 반환)
    */
    @Override
    @Transactional
    public String baseInsertReturnSelectKey(String sqlid, Map<String, Object> param)
    {
        return baseDao.baseInsertReturnSelectKey(sqlid, param);
    }


    /**
     * FuncName : baseInsert()
     * FuncDesc : 등록
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : int
    */
    @Override
    @Transactional
    public int baseInsert(String sqlid, Map<String, Object> param)
    {
        int iResult = 0;
        
        iResult = baseDao.baseInsert(sqlid, param);

        return iResult;
    }


    /**
     * FuncName : baseUpdate()
     * FuncDesc : 수정
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : int
    */
    @Override
    @Transactional
    public int baseUpdate(String sqlid, Map<String, Object> param)
    {
    	int iResult = 0;
        
        iResult = baseDao.baseUpdate(sqlid, param);

        return iResult;
    }


    /**
     * FuncName : baseDelete()
     * FuncDesc : 삭제
     * Param    : sqlid : SQL ID
     *          : param : SQL Parameter
     * Return   : int
    */
    @Override
    @Transactional
    public int baseDelete(String sqlid, Map<String, Object> param)
    {
    	int iResult = 0;
        
        iResult = baseDao.baseDelete(sqlid, param);

        return iResult;
    }

    /**
     * 함수명   : ajaxSave()
     * FuncDesc : Ajax C/U/D N건 처리
     * Param    : sqlCid : INSERT SQL ID
     *            sqlUid : UPDATE SQL ID
     *            sqlDid : DELETE SQL ID
     * Return   : int
    */
    @Override
    @Transactional
    public int ajaxSave(String sqlCid, String sqlUid, String sqlDid, JSONArray jsonArray) throws Exception
    {
        int iResult = 0;
        int iLength = jsonArray.length();

        for (int i=0; i<iLength; i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Map<String, Object> param = JsonUtil.JsonToMap(jsonObject.toString());

            System.out.println("ajaxSave c/u/d param = " + param.get("rowStatus").toString());

            if (param.get("rowStatus").toString().equals("C") == true) // Insert
            {
                iResult = baseInsert(sqlCid, param);
            }
            else if (param.get("rowStatus").toString().equals("U") == true) // Update
            {
                iResult = baseUpdate(sqlUid, param);
            }
            else if (param.get("rowStatus").toString().equals("D") == true) // Delete
            {
                iResult = baseDelete(sqlDid, param);
            }
        }

        return iResult;
    }
    
    /**
     * 함수명   : ajaxInsertKey()
     * FuncDesc : Ajax C/U/D N건 처리
     * Param    : sqlid : 첫번째 INSERT SQL ID
     *            param : 첫번째 INSERT Parameter Data
     *            jsonArray : C/U/D Parameter Data
     * Return   : String : idKey
    */
    @Override
    @Transactional
    public String ajaxInsertKey(String sqlid, Map<String, Object> param, JSONArray jsonArray) throws Exception
    {
        int iLength = jsonArray.length();
        
        String idKey = "";
        
        
        // 첫번째 Insert 처리 (Key값 저장)
        idKey = baseDao.baseInsertReturnSelectKey(sqlid, param);

        // Transaction C/U/D N건 처리
        for (int i=0; i<iLength; i++)
        {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            Map<String, Object> paramMap = JsonUtil.JsonToMap(jsonObject.toString());
            
            paramMap.put("idKey", idKey); // Key값 전달

            System.out.println("ajaxInsertKey c/u/d param = " + paramMap.get("rowStatus").toString());

            if (paramMap.get("rowStatus").toString().equals("C") == true) // Insert
            {
                baseDao.baseInsert(paramMap.get("sqlCid").toString(), paramMap);
            }
            else if (paramMap.get("rowStatus").toString().equals("U") == true) // Update
            {
                baseUpdate(paramMap.get("sqlUid").toString(), paramMap);
            }
            else if (paramMap.get("rowStatus").toString().equals("D") == true) // Delete
            {
                baseDelete(paramMap.get("sqlDid").toString(), paramMap);
            }
        }

        return idKey;
    }
    
    
    /**
     * 함수명   : insertKeyMultiTransaction()
     * FuncDesc : Insert Key Transaction C/U/D N건 처리
     * Param    : sqlid1     : C/U/D 1번 SQL ID
     *            sqlid2     : C/U/D 2번 SQL ID
     *            sqlid3     : C/U/D 3번 SQL ID
     *            sqlid4     : C/U/D 4번 SQL ID
     *            jsonArray1 : C/U/D 1번 JSONArray
     *            jsonArray2 : C/U/D 2번 JSONArray
     *            jsonArray3 : C/U/D 3번 JSONArray
     *            jsonArray4 : C/U/D 4번 JSONArray
     * Return   : int
    */
    @Override
    @Transactional
    public int insertKeyMultiTransaction(String sqlid1, String sqlid2, String sqlid3, String sqlid4, JSONArray jsonArray1, JSONArray jsonArray2, JSONArray jsonArray3, JSONArray jsonArray4) throws Exception
    {
        int iResult = 0;
        int iLength1 = jsonArray1.length();
        int iLength2 = jsonArray2.length();
        int iLength3 = jsonArray3.length();
        int iLength4 = jsonArray4.length();
        
        String idKey = ""; // Key값
        
        // 첫번째 처리
        for (int i=0; i<iLength1; i++)
        {
            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);
            
            Map<String, Object> param1 = JsonUtil.JsonToMap(jsonObject1.toString());
            
            // 첫번째 Insert 처리 (Key값 저장)
            idKey = baseDao.baseInsertReturnSelectKey(sqlid1, param1);
        }
        
        // 두번째 처리
        for (int i=0; i<iLength2; i++)
        {
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);

            Map<String, Object> param2 = JsonUtil.JsonToMap(jsonObject2.toString());
            
            param2.put("idKey", idKey);

            String rowStatus = (String) param2.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
                baseDao.baseInsert(sqlid2, param2);
            }
            else if (rowStatus.equals("U") == true) // Update
            {
                baseDao.baseUpdate(sqlid2, param2);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
                baseDao.baseDelete(sqlid2, param2);
            }

            iResult++;
        }

        // 세번째 처리
        for (int i=0; i<iLength3; i++)
        {
            JSONObject jsonObject3 = jsonArray3.getJSONObject(i);

            Map<String, Object> param3 = JsonUtil.JsonToMap(jsonObject3.toString());
            
            param3.put("idKey", idKey);

            String rowStatus = (String) param3.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
                baseDao.baseInsert(sqlid3, param3);
            }
            else if (rowStatus.equals("U") == true) // Update
            {
                baseDao.baseUpdate(sqlid3, param3);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
                baseDao.baseDelete(sqlid3, param3);
            }

            iResult++;
        }

        // 네번째 처리
        for (int i=0; i<iLength4; i++)
        {
            JSONObject jsonObject4 = jsonArray4.getJSONObject(i);

            Map<String, Object> param4 = JsonUtil.JsonToMap(jsonObject4.toString());
            
            param4.put("idKey", idKey);

            String rowStatus = param4.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
                baseDao.baseInsert(sqlid4, param4);
            }
            else if (rowStatus.equals("U") == true) // Update
            {
                baseDao.baseUpdate(sqlid4, param4);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
                baseDao.baseDelete(sqlid4, param4);
            }

            iResult++;
        }

        return iResult;
    }


    /**
     * FuncName : setMultiTransaction()
     * FuncDesc : Transaction C/U/D N건 처리
     * Param    : sqlid1     : C/U/D 1번 SQL ID
     *            sqlid2     : C/U/D 2번 SQL ID
     *            sqlid3     : C/U/D 3번 SQL ID
     *            sqlid4     : C/U/D 4번 SQL ID
     *            jsonArray1 : C/U/D 1번 JSONArray
     *            jsonArray2 : C/U/D 2번 JSONArray
     *            jsonArray3 : C/U/D 3번 JSONArray
     *            jsonArray4 : C/U/D 4번 JSONArray
     * Return   : int
    */
    @Override
    @Transactional
    public int setMultiTransaction(String sqlid1, String sqlid2, String sqlid3, String sqlid4, JSONArray jsonArray1, JSONArray jsonArray2, JSONArray jsonArray3, JSONArray jsonArray4) throws Exception
    {
        int iResult  = 0;
        int iLength1 = jsonArray1.length();
        int iLength2 = jsonArray2.length();
        int iLength3 = jsonArray3.length();
        int iLength4 = jsonArray4.length();

        // 첫번째 처리
        for (int i=0; i<iLength1; i++)
        {
            JSONObject jsonObject1 = jsonArray1.getJSONObject(i);

            Map<String, Object> param1 = JsonUtil.JsonToMap(jsonObject1.toString());

            String rowStatus = param1.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
					baseDao.baseInsert(sqlid1, param1);
			}
            else if (rowStatus.equals("U") == true) // Update
            {
            		baseDao.baseUpdate(sqlid1, param1);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
            		baseDao.baseDelete(sqlid1, param1);
            }

            iResult++;
        }

        // 두번째 처리
        for (int i=0; i<iLength2; i++)
        {
            JSONObject jsonObject2 = jsonArray2.getJSONObject(i);

            Map<String, Object> param2 = JsonUtil.JsonToMap(jsonObject2.toString());

            String rowStatus = param2.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
					baseDao.baseInsert(sqlid2, param2);
            }
            else if (rowStatus.equals("U") == true) // Update
            {
            		baseDao.baseUpdate(sqlid2, param2);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
            		baseDao.baseDelete(sqlid2, param2);
            }

            iResult++;
        }

        // 세번째 처리
        for (int i=0; i<iLength3; i++)
        {
            JSONObject jsonObject3 = jsonArray3.getJSONObject(i);

            Map<String, Object> param3 = JsonUtil.JsonToMap(jsonObject3.toString());

            String rowStatus = param3.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
					baseDao.baseInsert(sqlid3, param3);
            }
            else if (rowStatus.equals("U") == true) // Update
            {
            		baseDao.baseUpdate(sqlid3, param3);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
            		baseDao.baseDelete(sqlid3, param3);
            }

            iResult++;
        }

        // 네번째 처리
        for (int i=0; i<iLength4; i++)
        {
            JSONObject jsonObject4 = jsonArray4.getJSONObject(i);

            Map<String, Object> param4 = JsonUtil.JsonToMap(jsonObject4.toString());

            String rowStatus = param4.get("rowStatus").toString();
            
			if (rowStatus.equals("C") == true) // Insert
            {
					baseDao.baseInsert(sqlid4, param4);
			}
            else if (rowStatus.equals("U") == true) // Update
            {
            		baseDao.baseUpdate(sqlid4, param4);
            }
            else if (rowStatus.equals("D") == true) // Delete
            {
            		baseDao.baseDelete(sqlid4, param4);
            }

            iResult++;
        }

        return iResult;
    }
}
