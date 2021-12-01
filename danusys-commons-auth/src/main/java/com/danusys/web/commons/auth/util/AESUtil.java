package com.danusys.web.commons.auth.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;

/**
 * AES 암복호화 유틸
 * @author aple
 *
 */
public class AESUtil {

    private final static Logger logger = LoggerFactory.getLogger(AESUtil.class);
    private IvParameterSpec ivParamSpec;


    /**
     * 생성자
     * @param iv iv 초기화벡터 16자리 영숫자로 구성할 것
     */
    public AESUtil(String iv) throws UnsupportedEncodingException {
        byte[] ivBytes = iv.getBytes("UTF-8");
        this.ivParamSpec = new IvParameterSpec(ivBytes);
    }

    /**
     * 암호화
     * @param data 암호화할 문자열
     * @param encryptionKey 암호화에 사용될 키
     * @return 암호화된 문자열
     */
    public String encrypt(String data, String encryptionKey) {

        String enStr = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            cipher.init(1, key, this.ivParamSpec);
            byte[] cipherText = cipher.doFinal(data.getBytes("UTF-8"));
            enStr = DatatypeConverter.printHexBinary(cipherText);
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }finally{
            return enStr;
        }
    }
    /**
     * 복호화
     * @param encryptedData 암호화된 문자열
     * @param encryptionKey 암호화에 사용될 키
     * @return 복호화된 문자열
     */
    public String decrypt(String encryptedData, String encryptionKey) {

        String descStr = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
            cipher.init(2, key, this.ivParamSpec);
            byte[] cipherText = cipher.doFinal(DatatypeConverter.parseHexBinary(encryptedData));
            descStr = new String(cipherText, "UTF-8");
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }

        return descStr;
    }
}