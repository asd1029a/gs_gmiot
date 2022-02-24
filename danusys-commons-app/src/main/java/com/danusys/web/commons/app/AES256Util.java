package com.danusys.web.commons.app;

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

        byte[] keyBytes = new byte[16];
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
        return this.encrypt(str, null);
    }

    /**
     * AES256 으로 암호화 한다.
     *
     * @param str 암호화할 문자열
     * @param characterSet 문자열 Character set
     * @return
     */
    public String encrypt(String str, String characterSet)  {

        String enStr = null;
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
            if(characterSet == null) {
                byte[] encrypted = c.doFinal(str.getBytes());
                enStr = URLEncoder.encode(new String(Base64.encodeBase64(encrypted)), characterSet);
            } else {
                byte[] encrypted = c.doFinal(str.getBytes(characterSet));
                enStr = URLEncoder.encode(new String(Base64.encodeBase64(encrypted)), characterSet);
            }
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
        return decrypt(str, null);
    }

    /**
     * AES256으로 암호화된 txt 를 복호화한다.
     *
     * @param str
     *            복호화할 문자열
     * @return
     */
    public String decrypt(String str, String charterSet)  {
        String descStr = null;
        try {
            if(charterSet == null) {
                String decodingStr = URLDecoder.decode(str);
                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
                byte[] byteStr = Base64.decodeBase64(decodingStr.getBytes());
                descStr = new String(c.doFinal(byteStr));
            } else {
                String decodingStr = URLDecoder.decode(str, charterSet);
                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
                c.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(iv.getBytes()));
                byte[] byteStr = Base64.decodeBase64(decodingStr.getBytes());
                descStr = new String(c.doFinal(byteStr), charterSet);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            return descStr;
        }
    }
}
