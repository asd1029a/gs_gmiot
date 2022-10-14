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

    public static String RSA_SESSION_KEY = "__rsaPrivateKey__";

    private int KEY_SIZE = 4096;
    private PublicKey publicKey;
    private PrivateKey privateKey;
    private String publicKeyModulus;
    private String publicKeyExponent;
    KeyPairGenerator generator;

    public RsaUtils()  {
        init();
    }

    public void initialize(){
        this.init();
    }

    private void init() {
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

    public Optional<PrivateKey> privateKeyExtractionFrom(HttpSession session){
        PrivateKey privateKey = (PrivateKey) session.getAttribute(RSA_SESSION_KEY);
        if(validatePrivateKey(privateKey)){
            return Optional.of(privateKey);
        }
        return Optional.empty();
    }

    public void privateKeyDeleteFrom(HttpSession session){
        session.removeAttribute(RsaUtils.RSA_SESSION_KEY);
    }

    private boolean validatePrivateKey(PrivateKey privateKey) {
        return Objects.isNull(privateKey) ? false : true;
    }
}
