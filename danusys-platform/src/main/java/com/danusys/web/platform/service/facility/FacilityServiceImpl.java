package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.facility.FacilitySqlProvider;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FacilityServiceImpl implements FacilityService{

    public FacilityServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final FacilitySqlProvider fsp = new FacilitySqlProvider();

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, commonMapper.selectList(fsp.selectListQry(paramMap)));
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(fsp.selectOneQry(seq));
    }

    @Override
    public int insertOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(fsp.insertOptQry(paramMap));
    }

    @Override
    public int insert(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(fsp.insertQry(paramMap));
    }

    @Override
    public int update(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateQry(paramMap));
    }

    @Override
    public int updateOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateOptQry(paramMap));
    }

    @Override
    public void delete(int seq) throws Exception {
        commonMapper.delete(fsp.deleteQry(seq));
    }
}
