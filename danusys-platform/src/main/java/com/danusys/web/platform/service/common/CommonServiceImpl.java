package com.danusys.web.platform.service.common;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.CommonMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CommonServiceImpl implements CommonService {
    public CommonServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}
    private final CommonMapper commonMapper;

    @Override
    public Map<String, Object> selectCodeList(Map<String, Object> paramMap) throws Exception {
        System.out.println(commonMapper.selectCodeAll(paramMap));
        return PagingUtil.createPagingMap(paramMap, commonMapper.selectCodeAll(paramMap));
    }

    public EgovMap getCodeOne(int codeSeq) throws Exception {
        return commonMapper.selectCodeOne(codeSeq);
    }
}
