package com.danusys.web.commons.crypto.service.executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
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
@Service("AES128")
public class AES128Executor implements CryptoExecutor {
    private SecretKeySpec secretKeySpec;
    private String defaultCharSet = "UTF-8";
    private Cipher cipher;

    public AES128Executor() {
        try {
            this.cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String encrypt(String plainText, String key) {
        log.trace("AES128 encrypt plainText : {}, key : {}", plainText, key);
        String result = null;
        try {
            SecretKeySpec keySpec = createSecretKey(key);
            IvParameterSpec iv = createIv(key);
            this.cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv);
            byte[] encrypted = this.cipher.doFinal(plainText.getBytes(this.defaultCharSet));
//            result = URLEncoder.encode(new String(Base64.getEncoder().encode(encrypted)), defaultCharSet);
            result = Base64.getEncoder().encodeToString(encrypted);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }catch (BadPaddingException e) {
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
        log.trace("AES128 decrypt encodeText : {}, key : {}", encodeText, key);
        String result = null;
        try {
            SecretKeySpec keySpec = createSecretKey(key);
            IvParameterSpec iv = createIv(key);
            this.cipher.init(Cipher.DECRYPT_MODE, keySpec, iv);
            byte[] encrypted = this.cipher.doFinal(encodeText.getBytes(this.defaultCharSet));
            result = new String(Base64.getDecoder().decode(encrypted), this.defaultCharSet);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
        return result;
    }

    private SecretKeySpec createSecretKey(String key) {
        SecretKeySpec keySpec = null;

        try {
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes(this.defaultCharSet);
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

    private IvParameterSpec createIv(String key) {
        IvParameterSpec iv = null;

        try {
            byte[] keyBytes = new byte[16];
            byte[] b = key.getBytes(this.defaultCharSet);
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
