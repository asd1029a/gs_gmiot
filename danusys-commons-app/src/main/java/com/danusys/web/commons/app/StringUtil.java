package com.danusys.web.commons.app;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtil {
    public static String camelToSnake(String camelStr) {
        String regex = "([a-z])([A-Z]+)";
        String replacement = "$1_$2";
        String snakeStr = "";

        snakeStr = camelStr
                .replaceAll(
                        regex, replacement
                )
                .toLowerCase();
        return snakeStr;
    }

    public static String snakeToCamel(String snakeStr) {
        String camelStr = "";

        camelStr = snakeStr.substring(0, 1).toUpperCase()
                + snakeStr.substring(1);

        while (snakeStr.contains("_")) {
            camelStr = snakeStr
                    .replaceFirst(
                            "_[a-z]",
                            String.valueOf(
                                    Character.toUpperCase(
                                            snakeStr.charAt(
                                                    snakeStr.indexOf("_") + 1))));
        }
        return camelStr;

    }
    public static Timestamp stringToTimestamp(String dateStr) {
        String newDateStr = "";
        if(dateStr.length() == 16) {
            newDateStr = dateStr.concat(":00");
        } else if(dateStr.length() == 19) {
            newDateStr = dateStr;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt = LocalDateTime.parse(newDateStr, dtf);
        return Timestamp.valueOf(ldt);
    }
}
