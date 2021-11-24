package com.danusys.web.platform.common.util;

/**
 * Project : danusys-webservice-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/03
 * Time : 2:34 오후
 */
public class NumberUtils {

    public static Number toNumber(String text) throws NumberFormatException {

        if (text.indexOf(".") > 0) {
            try {
                return toNumber(text, Double.class);
            } catch (NumberFormatException e) {
                return toNumber(text, Float.class);
            }
        }

        try {
            return toNumber(text, Long.class);
        } catch (NumberFormatException e1) {
            try {
                return toNumber(text, Integer.class);
            } catch (NumberFormatException e2) {
                try {
                    return toNumber(text, Short.class);
                } catch (NumberFormatException e3) {
                    return toNumber(text, Byte.class);
                }
            }
        }
    }

    public static <T extends Number> T toNumber(String text, Class<T> targetClass) {
        if (text == null)
            throw new IllegalArgumentException("Input value must not be null");

        if (Double.class == targetClass)
            return targetClass.cast(Double.valueOf(text));
        if (Float.class == targetClass)
            return targetClass.cast(Float.valueOf(text));
        else if (Long.class == targetClass)
            return targetClass.cast(Long.valueOf(text));
        else if (Integer.class == targetClass)
            return targetClass.cast(Integer.valueOf(text));
        else if (Short.class == targetClass)
            return targetClass.cast(Short.valueOf(text));
        else if (Byte.class == targetClass)
            return targetClass.cast(Byte.valueOf(text));
        else
            throw new NumberFormatException(String.format("Text(%s) is not a number type", text));
    }
}
