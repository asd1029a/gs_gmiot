package com.danusys.web.platform.service.station;

import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.station.StationSqlProvider;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StationServiceImpl implements StationService{

    public StationServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final StationSqlProvider ssp = new StationSqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(ssp.selectListQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        List<EgovMap> debugger = commonMapper.selectList(ssp.selectListQryPaging(paramMap));
        EgovMap count = commonMapper.selectOne(ssp.selectCountQry(paramMap));
        pagingMap.put("data", commonMapper.selectList(ssp.selectListQryPaging(paramMap)));
        pagingMap.put("count", count.get("count"));
        pagingMap.put("statusCount", count);
        return PagingUtil.createPagingMap(paramMap, pagingMap);
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(ssp.selectOneQry(seq));
    }

    @Override
    public int add(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(ssp.insertQry(paramMap));
    }

    @Override
    public int mod(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(ssp.updateQry(paramMap));
    }

    @Override
    public void del(int seq) throws Exception {
        commonMapper.delete(ssp.deleteQry(seq));
    }
}
