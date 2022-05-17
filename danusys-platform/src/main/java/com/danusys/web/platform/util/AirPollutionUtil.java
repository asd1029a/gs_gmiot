package com.danusys.web.platform.util;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * packageName : com.danusys.web.platform.util
 * fileName : AirPollutionUtil
 * author : brighthoon94
 * date : 2022-05-16
 * description :
 * ===========================================================
 * DATE     AUTHOR      NOTE
 * -----------------------------------------------------------
 * 2022-05-16  brighthoon94      최초 생성
 */
public class AirPollutionUtil {
    public static Map<String, Object> convertAirPollutionDataToScoreMap(Double score500) throws Exception {
        double iP = score500;
                // convertAirPollutionDataToScore500(key, value);

        Map<String, ArrayList<Integer>> scoreMap = new HashMap<>();
        scoreMap.put("good", new ArrayList<>(Arrays.asList(0, 50, 100, 81)));
        scoreMap.put("normal", new ArrayList<>(Arrays.asList(51, 100, 80, 61)));
        scoreMap.put("bad", new ArrayList<>(Arrays.asList(101, 250, 60, 41)));
        scoreMap.put("tooBad", new ArrayList<>(Arrays.asList(251, 350, 40, 21)));
        scoreMap.put("danger", new ArrayList<>(Arrays.asList(351, 500, 20, 0)));
        Map<String, Object> resultMap = new HashMap<>();

        AtomicReference<Double> resultScore = new AtomicReference<>((double) 0);
        scoreMap.forEach((strKey, objValue) -> {
            double iLo = (double) objValue.get(0);
            double iHi = (double) objValue.get(1);
            double convertLo = (double) objValue.get(2);
            double convertHi = (double) objValue.get(3);
            if(betweenDoubleExclusive(iP, iLo, iHi)) {
                resultScore.set(convertLo + ((iP - iLo) * ((convertHi - convertLo) / (iHi - iLo))));
                resultMap.put("type", strKey);
                resultMap.put("score", resultScore.get().intValue());
            };
        });
        return resultMap;
    }

    public static Double convertAirPollutionDataToScore500(String key, Object value) throws Exception {
        double iHi = 0;
        double iLo = 0;
        double bpHi = 0;
        double bpLo = 0;
        double cP = Double.parseDouble(value.toString());
        Double resultScore = null;
        Map<String, List<Object>> scoreMap = new HashMap<>();

        scoreMap.put("so2Value", new ArrayList<>(Arrays.asList(0.0, 0.02, 0.021, 0.05, 0.051, 0.15, 0.151, 1.0)));
        scoreMap.put("coValue", new ArrayList<>(Arrays.asList(0.0, 2.0, 2.1, 9.0, 9.1, 15.0, 15.1, 50.0)));
        scoreMap.put("o3Value", new ArrayList<>(Arrays.asList(0.0, 0.03, 0.031, 0.09, 0.091, 0.15, 0.151, 0.6)));
        scoreMap.put("no2Value", new ArrayList<>(Arrays.asList(0.0, 0.03, 0.031, 0.06, 0.061, 0.2, 0.201, 2.0)));
        scoreMap.put("pm10Value24", new ArrayList<>(Arrays.asList(0.0, 30.0, 31.0, 80.0, 81.0, 150.0, 151.0, 600.0)));
        scoreMap.put("pm25Value24", new ArrayList<>(Arrays.asList(0.0, 15.0, 16.0, 35.0, 36.0, 75.0, 76.0, 500.0)));

        if(scoreMap.containsKey(key)) {
            if (betweenDoubleExclusive(cP, Double.parseDouble(String.valueOf(scoreMap.get(key).get(0))), Double.parseDouble(String.valueOf(scoreMap.get(key).get(1))))) {
                iHi = 50;
                bpHi = (double) scoreMap.get(key).get(1);
                bpLo = (double) scoreMap.get(key).get(0);
            } else if (betweenDoubleExclusive(cP, Double.parseDouble(String.valueOf(scoreMap.get(key).get(2))), Double.parseDouble(String.valueOf(scoreMap.get(key).get(3))))) {
                iLo = 51;
                iHi = 100;
                bpHi = (double) scoreMap.get(key).get(3);
                bpLo = (double) scoreMap.get(key).get(2);
            } else if (betweenDoubleExclusive(cP, Double.parseDouble(String.valueOf(scoreMap.get(key).get(4))), Double.parseDouble(String.valueOf(scoreMap.get(key).get(5))))) {
                iLo = 101;
                iHi = 250;
                bpHi = (double) scoreMap.get(key).get(5);
                bpLo = (double) scoreMap.get(key).get(4);
            } else if (betweenDoubleExclusive(cP, Double.parseDouble(String.valueOf(scoreMap.get(key).get(6))), Double.parseDouble(String.valueOf(scoreMap.get(key).get(7))))) {
                iLo = 251;
                iHi = 500;
                bpHi = (double) scoreMap.get(key).get(7);
                bpLo = (double) scoreMap.get(key).get(6);
            }
        }
        resultScore = ((((iHi - iLo) / (bpHi - bpLo)) * (cP - bpLo)) + iLo);
        return Math.floor(resultScore);
    }

    private static boolean betweenDoubleExclusive(double value, double min, double max) {
        boolean flag = false;
        if(Double.compare(value, min) >= 0) {
            flag = true;
        }
        if(Double.compare(value, max) > 0) {
            flag = false;
        }
        return flag;
    }
}
