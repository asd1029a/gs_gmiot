package com.danusys.smartmetering.common.util;

import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class AESUtil {

	private static final int keySize = 128;
	private static final int iterationCount = 500;
	private static String salt = "645eb82079752f1d3fd22043c48e46a25c6f18435b35b244e9152665c345a552";
	private static String iv = "d13ecda8abd2fad5a7f718f4779f0443";
	private static final String passPhrase = "danu1234!!";
	
	public static String encrypt(String plaintext) throws Exception {
		return encrypt(salt, iv, passPhrase, plaintext);
	}
	
	public static String decrypt(String ciphertext) throws Exception {
		return decrypt(salt, iv, passPhrase, ciphertext);
	}
	
	private static String encrypt(String salt, String iv, String passPhrase, String plaintext) throws Exception {
		SecretKey key = generateKey(salt, passPhrase);
		byte[] encrypted = doFinal(Cipher.ENCRYPT_MODE, key, iv, plaintext.getBytes("UTF-8"));
		return encodeBase64(encrypted);
	}

	private static String decrypt(String salt, String iv, String passPhrase, String ciphertext) throws Exception {
		SecretKey key = generateKey(salt, passPhrase);
		byte[] decrypted = doFinal(Cipher.DECRYPT_MODE, key, iv, decodeBase64(ciphertext));
		return new String(decrypted, "UTF-8");
	}

	private static byte[] doFinal(int encryptMode, SecretKey key, String iv, byte[] bytes) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(encryptMode, key, new IvParameterSpec(decodeHex(iv)));
		return cipher.doFinal(bytes);
	}

	private static SecretKey generateKey(String salt, String passPhrase) throws Exception {
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		KeySpec spec = new PBEKeySpec(passPhrase.toCharArray(), decodeHex(salt), iterationCount, keySize);
		SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
		return key;
	}
	
	private static String encodeBase64(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	private static byte[] decodeBase64(String str) {
		return Base64.decodeBase64(str);
	}

	private static String encodeHex(byte[] bytes) {
		return Hex.encodeHexString(bytes);
	}

	private static byte[] decodeHex(String str) throws Exception {
		return Hex.decodeHex(str.toCharArray());
	}
	/*
	private static String getRandomHexString(int length) {
		byte[] salt = new byte[length];
		new SecureRandom().nextBytes(salt);
		return encodeHex(salt);
	}
	*/
}