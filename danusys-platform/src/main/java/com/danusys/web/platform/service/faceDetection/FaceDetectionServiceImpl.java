package com.danusys.web.platform.service.faceDetection;

import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.commons.app.FileUtil;
import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.platform.dto.request.FaceDetectionRequestDto;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.faceDetection.FaceDetectionSqlProvider;
import lombok.RequiredArgsConstructor;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.LocalDateTime;
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
    public EgovMap getOne(String name) throws Exception {
        return commonMapper.selectOne(fdsp.selectOneNameQry(name));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int add(FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> paramMap = objectMapper.convertValue(faceDetectionRequestDto, Map.class);
        paramMap.put("insertUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
        paramMap.put("insertDt", Timestamp.valueOf(LocalDateTime.now()));

        return commonMapper.insert(fdsp.insertQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int mod(int faceSeq, FaceDetectionRequestDto faceDetectionRequestDto) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> paramMap = objectMapper.convertValue(faceDetectionRequestDto, Map.class);
        paramMap.put("faceSeq", faceSeq);
        paramMap.put("updateUserSeq", LoginInfoUtil.getUserDetails().getUserSeq());
        paramMap.put("updateDt", Timestamp.valueOf(LocalDateTime.now()));

        return commonMapper.update(fdsp.updateQry(paramMap));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void del(int seq) throws Exception {
        commonMapper.delete(fdsp.delete(seq));
    }
}
