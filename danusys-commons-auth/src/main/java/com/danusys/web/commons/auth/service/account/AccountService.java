package com.danusys.web.commons.auth.service.account;

import com.danusys.web.commons.util.EgovMap;

import java.util.Map;

public interface AccountService {
    EgovMap getListUser(Map<String, Object> paramMap) throws Exception;
    EgovMap getOneUser(int seq) throws Exception;
}
