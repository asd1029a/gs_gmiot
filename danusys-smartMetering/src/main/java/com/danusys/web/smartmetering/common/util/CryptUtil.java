package com.danusys.web.smartmetering.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

public class CryptUtil {
	
	public static final String encodeMD5HexBase64(String pw){
		return new String(Base64.encodeBase64(DigestUtils.md5Hex(pw).getBytes()));
	}
	
}
