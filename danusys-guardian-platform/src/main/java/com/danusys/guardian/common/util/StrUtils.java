package com.danusys.guardian.common.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project : danusys-guardian-parent
 * Created by IntelliJ IDEA
 * Developer : kai
 * Date : 2021/11/04
 * Time : 1:00 오후
 */
public class StrUtils {

    private static final String EMAIL_PATTERN = "([\\w.])(?:[\\w.]*)(@.*)";
    private static final String LASTNAME_PATTERN = "(?<=.{0}).";
    private static final String LAST_6_CHAR_PATTERN = "(.{6}$)";
    private static final String LAST_1_CHAR_PATTERN = "(.{1}$)";


    public static int getInt(String str) {
        return getInt(str, 0);
    }

    public static int getInt(String str, int nDefault) {
        try {
            int nResult = Integer.parseInt(str);
            return nResult;
        } catch (Exception ex) {
            return nDefault;
        }
    }

    public static int getInt(Object obj) {
        return getInt(obj, 0);
    }

    public static int getInt(Object obj, int nDefault) {

        int nResult = nDefault;
        try {
            if (obj instanceof Integer) {
                nResult = (Integer)obj;
            } else  if (obj instanceof String) {
                nResult = Integer.parseInt((String)obj);
            } else  {
                return  nResult;
            }
            return nResult;
        } catch (Exception ex) {
            return nDefault;
        }
    }

    public static String getStr(Object obj) {
        return getStr(obj, "");
    }

    public static String getStr(Object obj, String nDefault) {

        if (obj == null) {
            return nDefault;
        }

        String result = nDefault;
        try {
            result = String.valueOf(obj);

            if (obj instanceof String) {
                return result;
            } else  {
                return  String.valueOf(obj);
            }
        } catch (Exception ex) {
            return nDefault;
        }
    }

    public static String getStrIfEmpty(Object obj, String nDefault) {
        String str = getStr(obj, nDefault);
        if (StringUtils.isEmpty(str)) {
            return nDefault;
        } else {
            return str;
        }
    }

    public static String getNumberString(String strSrc) {
        if (StringUtils.isEmpty(strSrc)) {
            return strSrc;
        }
        return  strSrc.replaceAll("[a-zA-Z\\-+.]", "");
    }

    public static String getEmptyIfNull( String strInput, String strDefault ){
        String str = getEmptyIfNull( strInput );
        if ( str.equals("") ) {
            return strDefault.trim();
        } else {
            return str;
        }
    }
    public static String getEmptyIfNull( String strInput ){
        String strReturn = "";
        if ( strInput != null && strInput.trim().length() > 0 ) {
            strReturn=strInput;
        }
        return strReturn;
    }

    public static boolean isTrue( String strInput ){
        String tmp = getEmptyIfNull(strInput);
        if (tmp != "" && tmp.equals("true")) {
            return true;
        }
        return false;
    }

    /**
     * HTML tag를 제거하는 함수
     *
     * @param strSource
     * @return
     */
    public static String stripHtml(String strSource) {

        String strResult = strSource;
        if (org.apache.commons.lang3.StringUtils.isEmpty(strSource)) {
            return strSource;
        }

        String strRegEx = "<[^>]*>";
        strResult =  strSource.replaceAll(strRegEx, "");
        return strResult;
    }

    /**
     * Date 형 스트링으로 부터 Date를 변환하는 함수
     *
     * @param strDate 변경할 문자형 날자
     * @return
     * @throws Exception
     */
    public static Date getDateFromString(String strDate) throws Exception {
        String strCleanDate = getNumbersFromString(strDate);
        SimpleDateFormat transFormat = new SimpleDateFormat("yyyyMMdd");
        Date dtTo = transFormat.parse(strCleanDate);
        return dtTo;
    }

    /**
     * 입력문자로 부터 숫자가 아닌것은 모두 제거
     *
     * @param strDate 변경할 문자형 날자
     * @return
     */
    public static String getNumbersFromString(String strDate) {
        String strCleanDate = strDate.replaceAll("[^0-9]", "");
//        String strCleanDate = strDate.replaceAll("[^\\d]", "");
//        String strCleanDate = strDate.replaceAll("\\D", "");
        return strCleanDate;
    }


//    /**
//     * 시작날자부터 끝날자까지의 일수 차를 구한다.
//     *
//     * @param strFromDate  시작일자
//     * @param strToDate    종료일자
//     * @return
//     */
//    public static int getDiffsFromString(String strFromDate, String strToDate) {
//
//        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd");
//        DateTime start = formatter.parseDateTime(getNumbersFromString(strFromDate));
//        DateTime end = formatter.parseDateTime(getNumbersFromString(strToDate));
//
//        // Get days between the start date and end date.
//        int nDays = Days.daysBetween(start, end).getDays();
//
//        return nDays;
//    }

    /**
     *  문자 마스킹 처리
     *
     * @param strSrc   입력문자
     * @param strPattern    패턴
     * @param strMaskPattern   바꾸고자하는 패턴
     * @return
     */
    public static String maskString(String strSrc, String strPattern, String strMaskPattern) {
        if (StringUtils.isEmpty(strSrc)) {
            return strSrc;
        }
        return strSrc.replaceAll(strPattern, strMaskPattern);
    }

    /**
     * 영문 이름 마스킹
     *
     * @param strSrc
     * @return
     */
    public static String maskLastname(String strSrc) {
        return maskString(strSrc, LASTNAME_PATTERN, "*");
    }

    /**
     * 이메일 마스킹
     *
     * @param strSrc
     * @return
     */
    public static String maskEmail(String strSrc) {
        return maskString(strSrc, EMAIL_PATTERN, "$1****$2");
    }

    /**
     * 주민번호, 여권번호 마스킹
     *
     * @param strSrc
     * @return
     */
    public static String maskPublicNumber(String strSrc) {
        return maskString(strSrc, LAST_6_CHAR_PATTERN, "******");
    }

    /**
     * 한글 이름 마스크
     *
     * @param strSrc
     * @return
     */
    public static String maskKoreanName(String strSrc) {
        return maskString(strSrc, LAST_1_CHAR_PATTERN, "*");
    }


    /**
     * 휴대폰 번호 마스킹 처리
     * @param phoneNum
     * @return maskedCellPhoneNumber
     */
    public static String maskPhoneNum(String phoneNum) {
        /*
         * 요구되는 휴대폰 번호 포맷
         * 01055557777 또는 0113339999 로 010+네자리+네자리 또는 011~019+세자리+네자리 이!지!만!
         * 사실 0107770000 과 01188884444 같이 가운데 번호는 3자리 또는 4자리면 돈케어
         * */
        String regex = "(01[016789])(\\d{3,4})\\d{4}$";
        Matcher matcher = Pattern.compile(regex).matcher(phoneNum);
        if (matcher.find()) {
            String replaceTarget = matcher.group(2);
            char[] c = new char[replaceTarget.length()];
            Arrays.fill(c, '*');
            return phoneNum.replace(replaceTarget, String.valueOf(c));
        }
        return phoneNum;
    }

    /**
     * 폰번호 포맷으로 변환
     *
     * @param phoneNumber
     * @return
     */
    public static String formatPhoneNum(String phoneNumber) {

        phoneNumber = getNumbersFromString(phoneNumber);

        String regEx = "(\\d{3})(\\d{3,4})(\\d{4})";
        if(!Pattern.matches(regEx, phoneNumber)) {
            return null;
        }
        return phoneNumber.replaceAll(regEx, "$1-$2-$3");
    }

    public static String formatFixLength(int value, int length) {
        String strOutput = String.format("%0" + length + "d", value);
        return strOutput;
    }

    public static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%" + n + "s", s);
    }

    public static String padLeftString(String s, int n, String padStr) {
        return StringUtils.leftPad(s, n, "0");
    }


}
