package com.danusys.web.platform.util;

import java.util.HashMap;
import java.util.Map;

public class SqlUtil {

    public static Map<String, Object> getInsertValuesStr(Map<String, Object> paramMap){
        StringBuilder keys = new StringBuilder();
        StringBuilder values = new StringBuilder();
        Map<String, Object> resultMap = new HashMap<String, Object>();

        paramMap.forEach((key, value) -> {
            if(keys.length() > 0) {
                keys.append(",");
                values.append(",");
            }
            keys.append(StringUtil.camelToSnake(key));
            values.append("'").append(value).append("'");
        });
        resultMap.put("columns", keys.toString());
        resultMap.put("values", values.toString());

        return resultMap;
    }

    public static String getMultiSetStr(Map<String, Object> paramMap){
        StringBuilder setStr = new StringBuilder();

        paramMap.entrySet()
                .stream()
                .filter(t -> !t.getKey().equals("noticeSeq"))
                .forEach((entry) -> {
                            if(setStr.length() > 0) {
                                setStr.append(",");
                            }
                            setStr.append(StringUtil.camelToSnake(entry.getKey()))
                                    .append(" = '")
                                    .append(entry.getValue().toString())
                                    .append("'");
                        }
                );
        return setStr.toString();
    }
}
