package com.danusys.web.platform.service.faceDetection;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.faceDetection.FaceDetectionSqlProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FaceDetectionServiceImpl implements FaceDetectionService {
    private final CommonMapper commonMapper;
    private final FaceDetectionSqlProvider fdsp = new FaceDetectionSqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap resultMap = new EgovMap();
        resultMap.put("data", commonMapper.selectList(fdsp.selectListQry(paramMap)));
        return resultMap;
    }

    @Override
    public EgovMap getListPaging(Map<String, Object> paramMap) throws Exception {
        Map<String, Object> pagingMap = new HashMap<>();
        EgovMap count = commonMapper.selectOne(fdsp.selectCountQry(paramMap));
        pagingMap.put("data", commonMapper.selectList(fdsp.selectListQry(paramMap)));
        pagingMap.put("count", count.get("count"));
        return PagingUtil.createPagingMap(paramMap, pagingMap);
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(fdsp.selectOneQry(seq));
    }

    @Override
    public int add(Map<String, Object> paramMap) throws Exception {
        return 0;
    }

    @Override
    public int mod(Map<String, Object> paramMap) throws Exception {
        return 0;
    }

    @Override
    public void del(int seq) throws Exception {

    }
}
