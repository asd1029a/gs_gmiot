package com.danusys.web.platform.service.facilityOpt;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.facilityOpt.FacilityOptSqlProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacilityOptServiceImpl implements FacilityOptService {

    public FacilityOptServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final FacilityOptSqlProvider ssp = new FacilityOptSqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(ssp.selectListQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        pagingMap.put("data", commonMapper.selectList(ssp.selectListQry(paramMap)));
        EgovMap count = commonMapper.selectOne(ssp.selectCountQry(paramMap));
        pagingMap.put("count", count.get("count"));
        return PagingUtil.createPagingMap(paramMap, pagingMap);
    }
}
