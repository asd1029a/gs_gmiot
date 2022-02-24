package com.danusys.web.platform.service.facility;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.facility.FacilitySqlProvider;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FacilityServiceImpl implements FacilityService{

    public FacilityServiceImpl(CommonMapper commonMapper) {this.commonMapper = commonMapper;}

    private final CommonMapper commonMapper;
    private final FacilitySqlProvider fsp = new FacilitySqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        if(paramMap.get("draw") != null) {
            Map<String, Object> pagingMap = new HashMap<>();
            pagingMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
            pagingMap.put("count", commonMapper.selectOne(fsp.selectCountQry(paramMap)).get("count"));
            return PagingUtil.createPagingMap(paramMap, pagingMap);
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(fsp.selectListQry(paramMap)));
            return resultMap;
        }
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(fsp.selectOneQry(seq));
    }

    @Override
    public int add(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(fsp.insertQry(paramMap));
    }

    @Override
    public int addOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(fsp.insertOptQry(paramMap));
    }

    @Override
    public int mod(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateQry(paramMap));
    }

    @Override
    public int modOpt(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(fsp.updateOptQry(paramMap));
    }

    @Override
    public void del(int seq) throws Exception {
        commonMapper.delete(fsp.deleteQry(seq));
    }
}
