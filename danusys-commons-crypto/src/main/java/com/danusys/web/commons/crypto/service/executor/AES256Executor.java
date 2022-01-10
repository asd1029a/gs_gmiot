package com.danusys.web.commons.crypto.service.executor;

import com.danusys.web.commons.util.AES256Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo9
 * Date : 2022/01/07
 * Time : 15:20
 */
@Slf4j
@Service("AES256")
public class AES256Executor implements CryptoExecutor {
    private SecretKeySpec secretKeySpec;

    public AES256Executor() {

    }

    @Override
    public String encrypt(String plainText, String key) {
        log.trace("AES256 encrypt plainText : {}, key : {}", plainText, key);
        String result = null;
        String characterSet = "UTF-8";
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = createSecretKey(key);
            IvParameterSpec iv = createIv(key);
            c.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = c.doFinal(plainText.getBytes(characterSet));
//            result = URLEncoder.encode(new String(Base64.getEncoder().encode(encrypted)), characterSet);
            result = Base64.getEncoder().encodeToString(encrypted);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String decrypt(String encodeText, String key) {
        log.trace("AES256 decrypt encodeText : {}, key : {}", encodeText, key);
        String result = null;
        String characterSet = "UTF-8";
        try {
            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = createSecretKey(key);
            IvParameterSpec iv = createIv(key);
            c.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted = c.doFinal(encodeText.getBytes(characterSet));
//            result = URLEncoder.encode(new String(Base64.getEncoder().encode(encrypted)), characterSet);
            result = new String(Base64.getDecoder().decode(encrypted), characterSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

    public SecretKeySpec createSecretKey(String key) {
        SecretKeySpec keySpec = null;

        try {
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            keySpec = new SecretKeySpec(keyBytes, "AES");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return keySpec;
    }

    public IvParameterSpec createIv(String key) {
        IvParameterSpec iv = null;

        try {
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes("UTF-8");
            int len = b.length;
            if (len > keyBytes.length)
                len = keyBytes.length;
            System.arraycopy(b, 0, keyBytes, 0, len);
            iv = new IvParameterSpec(keyBytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return iv;
    }
}
