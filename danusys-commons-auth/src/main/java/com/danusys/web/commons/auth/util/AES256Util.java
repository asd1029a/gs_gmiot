package com.danusys.web.commons.auth.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;

/**
 * 양방향 암호화 알고리즘인 AES256 암호화를 지원하는 클래스
 */
public class AES256Util {

    private static final Logger log = LoggerFactory.getLogger(AES256Util.class);

    private String iv;
    private Key keySpec;

    public AES256Util(String key) throws UnsupportedEncodingException {
        this.iv = key.substring(0, 16);

        byte[] keyBytes = new byte[32];
        byte[] b = key.getBytes("UTF-8");
        int len = b.length;
        if(len > keyBytes.length)
            len = keyBytes.length;
        System.arraycopy(b, 0, keyBytes, 0, len);
        SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");

        this.keySpec = keySpec;

        log.trace("this.iv[" + this.iv +"]");
        log.trace("this.iv[" + key +"]");
    }

    /**
     * AES256 으로 암호화 한다.
     *
     * @param str
     *            암호화할 문자열
     * @return
     */
    public String encrypt(String str)  {

        String enStr = null;
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            byte[] encrypted = c.doFinal(str.getBytes("UTF-8"));
            enStr = URLEncoder.encode(new String(Base64.encodeBase64(encrypted)), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return enStr;
        }

    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     *
     * @param str
     *            복호화할 문자열
     * @return
     */
    public String decrypt(String str)  {
        String descStr = null;
        try {
            String decodingStr = URLDecoder.decode(str, "UTF-8");
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            byte[] byteStr = Base64.decodeBase64(decodingStr.getBytes());
            descStr = new String(c.doFinal(byteStr), "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return descStr;
        }
    }
}
