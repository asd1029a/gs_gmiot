package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.facility.FacilityMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FacilityServiceImpl implements FacilityService{

    public FacilityServiceImpl(FacilityMapper facilityMapper) {this.facilityMapper = facilityMapper;}

    private final FacilityMapper facilityMapper;

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, facilityMapper.selectList(paramMap));
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return facilityMapper.selectOne(seq);
    }

    @Override
    public int insert(Map<String, Object> paramMap) throws Exception {
        return facilityMapper.insert(paramMap);
    }

    @Override
    public int update(Map<String, Object> paramMap) throws Exception {
        return facilityMapper.update(paramMap);
    }

    @Override
    public void delete(int seq) throws Exception {

    }
}
