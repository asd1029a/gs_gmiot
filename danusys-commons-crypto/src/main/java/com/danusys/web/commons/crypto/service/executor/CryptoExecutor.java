package com.danusys.web.commons.crypto.service.executor;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo9
 * Date : 2022/01/06
 * Time : 16:54
 */
public interface CryptoExecutor {
    String encrypt(String plainText, String key);

    String decrypt(String encodeText, String key);
}
