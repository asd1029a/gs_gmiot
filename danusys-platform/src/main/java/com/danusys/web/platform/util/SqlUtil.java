package com.danusys.web.platform.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class SqlUtil {

    public static Map<String, Object> getInsertValuesStr(Map<String, Object> paramMap){
        Map<String, Object> resultMap = new HashMap<String, Object>();

        resultMap.put("columns", String.join(",", paramMap.keySet()));
        resultMap.put("values", paramMap.values().stream()
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
