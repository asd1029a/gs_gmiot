package com.danusys.web.commons.auth.service.account;

import com.danusys.web.commons.auth.mapper.account.AccountSqlProvider;
import com.danusys.web.commons.auth.mapper.common.CommonMapper;
import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.commons.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService{

    public AccountServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final AccountSqlProvider asp = new AccountSqlProvider();

    @Override
    public EgovMap getListUser(Map<String, Object> paramMap) throws Exception {

        if (paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(asp.selectListUserQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(asp.selectCountUserQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(asp.selectListUserQry(paramMap)));
            return resultMap;
        }
    }

    public EgovMap getOneUser(int seq) throws Exception {
        return commonMapper.selectOne(asp.selectOneUserQry(seq));
    }
}
