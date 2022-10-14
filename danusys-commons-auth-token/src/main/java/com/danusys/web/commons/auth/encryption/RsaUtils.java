package com.danusys.web.commons.auth.encryption;

import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 *
 */

@Component
public class RsaUtils {

    public static String RSA_PRIVATE_KEY = "__rsaPrivateKey__";

    private int KEY_SIZE = 4096;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String publicKeyModulus;
    private String publicKeyExponent;
    KeyPairGenerator generator;

    public RsaUtils()  {
        try{
            generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(KEY_SIZE);
            KeyPair keyPair = generator.genKeyPair();

            publicKey = keyPair.getPublic();
            privateKey = keyPair.getPrivate();

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);

            publicKeyModulus = keySpec.getModulus().toString(16);
            publicKeyExponent = keySpec.getPublicExponent().toString(16);
        } catch( NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            publicKey = null;
            privateKey = null;
            publicKeyExponent = null;
            publicKeyModulus = null;
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public String getPublicKeyModulus() {
        return publicKeyModulus;
    }

    public String getPublicKeyExponent() {
        return publicKeyExponent;
    }

    public Optional<String> decrypt(PrivateKey privateKey, String securityCode){
        String decryptCode = Strings.EMPTY;

        try{
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            byte[] encryptedBytes = Funcs.hexToByteArray(securityCode);
            if(encryptedBytes == null) return Optional.empty();

            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            decryptCode = new String(decryptedBytes, "utf-8");
        }catch(Exception e){
            e.printStackTrace();
        }
        return Optional.of(decryptCode);
    }

    public PrivateKey privateKeyExtraction(HttpServletRequest request){
        HttpSession session = request.getSession();
        PrivateKey privateKey = (PrivateKey) session.getAttribute(RsaUtils.RSA_PRIVATE_KEY);

        if(!valiedatePrivateKey(privateKey)){
            session.removeAttribute(RsaUtils.RSA_PRIVATE_KEY);
            return privateKey;
        }

        return privateKey;
    }

    private boolean valiedatePrivateKey(PrivateKey privateKey) {
        return Objects.isNull(privateKey) ? true : false;
//        if(privateKey == null){
//            throw new RuntimeException("암호화 비밀키를 찾을 수 없습니다.");
//        }
    }
}
