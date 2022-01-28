package com.danusys.web.platform.service.station;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.station.StationMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class StationServiceImpl implements StationService{

    public StationServiceImpl(StationMapper stationMapper) {this.stationMapper = stationMapper;}

    private final StationMapper stationMapper;

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, stationMapper.selectList(paramMap));
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return stationMapper.selectOne(seq);
    }

    @Override
    public int insert(Map<String, Object> paramMap) throws Exception {
        return stationMapper.insert(paramMap);
    }

    @Override
    public int update(Map<String, Object> paramMap) throws Exception {
        return stationMapper.update(paramMap);
    }

    @Override
    public void delete(int seq) throws Exception {

    }
}
