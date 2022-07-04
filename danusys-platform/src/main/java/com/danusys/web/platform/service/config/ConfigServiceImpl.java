package com.danusys.web.platform.service.config;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.config.ConfigSqlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {

    private final CommonMapper commonMapper;
    private final ConfigSqlProvider csp = new ConfigSqlProvider();

    @Override
    public EgovMap getListCode(Map<String, Object> paramMap) throws Exception {

        if (paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(csp.selectListCodeQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(csp.selectCountCodeQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(csp.selectListTypeQry(paramMap)));
            return resultMap;
        }
    }

    public EgovMap getOneCode(int seq) throws Exception {
        return commonMapper.selectOne(csp.selectOneCodeQry(seq));
    }

    @Override
    public int addCode(Map<String, Object> paramMap) throws Exception {
        paramMap.put("insertUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
        return commonMapper.insert(csp.insertCodeQry(paramMap));
    }

    @Override
    public int modCode(Map<String, Object> paramMap) throws Exception {
        paramMap.put("updateUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
        return commonMapper.update(csp.updateCodeQry(paramMap));
    }

    @Override
    public void delCode(int seq) throws Exception {
        commonMapper.delete(csp.deleteCodeQry(seq));
    }

    @Override
    public EgovMap getOneEventKind(String pKind) {
        return commonMapper.selectOne(csp.selectOneEventKindQry(pKind));
    }

    @Override
    public List<EgovMap> getListMntrInitParam(String pageTypeCodeValue) throws Exception {
        return commonMapper.selectList(csp.selectListInitMntrParam(pageTypeCodeValue));
    }

    @Override
    public EgovMap getVideoNetInfo(String ipClassAB) throws Exception {
        return commonMapper.selectOne(csp.selectOneVideoNetInfoQry(ipClassAB));
    }

    @Override
    public List<EgovMap> getVideoConfig() throws Exception {
        return commonMapper.selectList(csp.selectOneVideoConfigQry());
    }
}
