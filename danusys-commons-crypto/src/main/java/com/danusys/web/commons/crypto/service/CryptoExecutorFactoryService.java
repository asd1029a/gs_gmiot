package com.danusys.web.commons.crypto.service;

import com.danusys.web.commons.crypto.service.executor.CryptoExecutor;
import com.danusys.web.commons.crypto.service.executor.CryptoExecutorFactory;
import com.danusys.web.commons.crypto.types.CryptoType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo9
 * Date : 2022/01/10
 * Time : 17:04
 */
@Slf4j
@Service
public class CryptoExecutorFactoryService {
    private CryptoExecutorFactory cryptoExecutorFactory;

    public CryptoExecutorFactoryService(CryptoExecutorFactory cryptoExecutorFactory) {
        this.cryptoExecutorFactory = cryptoExecutorFactory;
    }

    public String encrypt(CryptoType cryptoType, String plainText, String key) {
        CryptoExecutor cryptoExecutor = cryptoExecutorFactory.create(cryptoType);

        return cryptoExecutor.encrypt(plainText, key);
    }

    public String decrypt(CryptoType cryptoType, String plainText, String key) {
        CryptoExecutor cryptoExecutor = cryptoExecutorFactory.create(cryptoType);

        return cryptoExecutor.decrypt(plainText, key);
    }
}
