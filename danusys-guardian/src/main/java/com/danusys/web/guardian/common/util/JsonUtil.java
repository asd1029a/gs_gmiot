package com.danusys.web.guardian.common.util;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.util.*;

/*
//  클래스  명 : JsonUtil
//  클래스설명 : JsonUtil 클래스
*/
public class JsonUtil
{
    // 생성자
    public JsonUtil()
    {
        // 생성자 Code
    }

    /**
     * FuncName : JsonToMap()
     * FuncDesc : Json String -> Map 형태 변환
     * Param    : param : Json String
     * Return   : Map<String, Object>
    */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> JsonToMap(String param) throws IOException {

//        Gson gson = new Gson();
//
//        // 테스트 코드
//        
//        Map<String, Object> map = gson.fromJson(param, new HashMap<String,Object>().getClass());
//
//        Set<Entry<String, Object>> ms = map.entrySet();
//
//
//        for (Entry<String, Object> e:ms)
//        {
//            System.out.println(e.getKey() + " : " + e.getValue());
//        }
//        

        //return gson.fromJson(param, new HashMap<String,Object>().getClass());
		ObjectMapper mapper = new ObjectMapper();

//		Map<String, Object> map = mapper.readValue(param, new TypeReference<HashMap<String,Object>>() {});
//
//		Set<Map.Entry<String, Object>> ms = map.entrySet();
//        for (Map.Entry<String, Object> e:ms)
//        {
//            map.put(e.getKey(), cleanXssDecode(String.valueOf(e.getValue())));
//        }
//
//        return map;
		return mapper.readValue(param, new TypeReference<Map<String,Object>>() {});
    }


    /**
     * FuncName : ListToJson()
     * FuncDesc : List -> Json String 변환
     * Param    : res : Json String
     * Return   : Json String
    */
    @SuppressWarnings("unchecked")
    public static String ListToJson(List<Map<String, Object>> res) throws IOException {

        //Gson gson = new Gson();

        // 테스트 코드
        /*
        Map<String, Object> param = gson.fromJson(param, new HashMap<String,Object>().getClass());

        Set<Entry<String, Object>> ms = param.entrySet();

        for (Entry<String, Object> e:ms)
        {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        */


        //return gson.toJson(res);




		ObjectMapper mapper = new ObjectMapper();

		List<Map<String, Object>> result = res;


//			Map<String, Object> ms = result.get(i);
//			for (Map.Entry<String, Object> entry : ms.entrySet()) {
//
//				//System.out.println(entry.getKey() + ": " + entry.getValue());
//			}


		for(int i=0; i<result.size(); i++)
		{
			Map<String, Object> resultMap = mapper.convertValue(result.get(i), Map.class);
			Set<Map.Entry<String, Object>> ms = resultMap.entrySet();
			for (Map.Entry<String, Object> e:ms)
			{
				resultMap.put(e.getKey(), CommonUtil.getReXSSFilter(String.valueOf(e.getValue())));
				//System.out.println(e.getKey() + " : " + e.getValue());
			}
			result.set(i, resultMap);
		}
		//Collections.replaceAll(result, result.

		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result) ;
    }

    /**
	 * Json을 HashMap 으로 변환
	 * @param param
	 * @return
	 */
    @SuppressWarnings("unchecked")
	public static HashMap<String, Object> JsonToHashMap(String param) throws IOException {
		//Gson gson = new Gson();
		//return gson.fromJson(param, new HashMap<String, Object>().getClass());
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(param, new TypeReference<HashMap<String,Object>>() {});
	}


    /**
     * FuncName : JsonToLinkedHashMap()
     * FuncDesc : Json String -> LinkedHashMap 형태 변환(들어온 순서대로)
     * Param    : param : Json String
     * Return   : Map<String, Object>
    */
    @SuppressWarnings("unchecked")
    public static LinkedHashMap<String, Object> JsonToLinkedHashMap(String param) throws IOException {
        //Gson gson = new Gson();
        // 테스트 코드
        /*
        Map<String, Object> map = gson.fromJson(param, new HashMap<String,Object>().getClass());

        Set<Entry<String, Object>> ms = map.entrySet();


        for (Entry<String, Object> e:ms)
        {
            System.out.println(e.getKey() + " : " + e.getValue());
        }
        */

        //return gson.fromJson(param, new LinkedHashMap<String,Object>().getClass());
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(param, new TypeReference<LinkedHashMap<String,Object>>() {});
    }


    /**
     * FuncName : OneStringToJson()
     * FuncDesc : Json String 변환
     * Param    : sData : String
     * Return   : String
    */
    @SuppressWarnings("unchecked")
    public static String OneStringToJson(String sData) throws IOException {
        //Gson gson = new Gson();

        //return gson.toJson(sData);

        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(sData) ;
    }

    /**
     * FuncName : HashMapToJson()
     * FuncDesc : Json String 변환
     * Param    : sData : String
     * Return   : String
    */
    @SuppressWarnings("unchecked")
    public static String HashMapToJson(HashMap<String, Object> map) throws IOException {
        //Gson gson = new Gson();

        //return gson.toJson(map);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map) ;
    }
    
    /**
     * FuncName : MapToJson()
     * FuncDesc : Json String 변환
     * Param    : sData : String
     * Return   : String
     */
    @SuppressWarnings("unchecked")
    public static String MapToJson(Map<String, Object> map) throws IOException {
        /*Gson gson = new Gson();
        
        return gson.toJson(map);*/
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map) ;
    }
}
