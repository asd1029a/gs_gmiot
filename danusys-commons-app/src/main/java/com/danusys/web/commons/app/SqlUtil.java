package com.danusys.web.commons.app;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlUtil {
    public static Map<String, Object> getInsertValuesStr(Map<String, Object> paramMap){
        Map<String, Object> resultMap = new HashMap<String, Object>();
        Map<String, Object> columnMap = new HashMap<String, Object>();

        paramMap.forEach((k, v) -> columnMap.put(StringUtil.camelToSnake(k), v));

        resultMap.put("columns", String.join(",", columnMap.keySet()));
        resultMap.put("values", columnMap.values().stream()
                .map(value -> {
                    if (value instanceof String) {
                        return "'" + value + "'";
                    } else if (value instanceof Integer) {
                        return String.valueOf(value);
                    } else {
                        return "'" + value + "'";
                    }
                })
                .collect(Collectors.joining(",")));
        return resultMap;
    }

    public static String getMultiSetStr(Map<String, Object> paramMap){
        StringBuilder setStr = new StringBuilder();

        paramMap.entrySet()
                .stream()
                .filter(t -> !t.getKey().toLowerCase().equals("seq"))
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
