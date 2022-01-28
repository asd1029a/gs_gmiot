package com.danusys.web.commons.crypto.service.executor;

import com.danusys.web.commons.crypto.types.CryptoType;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ippo9
 * Date : 2022/01/06
 * Time : 16:55
 */
public interface CryptoExecutorFactory {
    CryptoExecutor create(CryptoType cryptoType);
}
