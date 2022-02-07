package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.common.CommonMapper;
import com.danusys.web.platform.mapper.notice.NoticeSqlProvider;
import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;
import com.danusys.web.platform.util.Paging;
import com.danusys.web.platform.util.PagingUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    public NoticeServiceImpl(CommonMapper commonMapper) {
        this.commonMapper = commonMapper;
    }

    private final CommonMapper commonMapper;
    private final NoticeSqlProvider nsp = new NoticeSqlProvider();

    @Override
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        if(paramMap.get("draw") != null) {
            return PagingUtil.createPagingMap(paramMap, commonMapper.selectList(nsp.selectListQry(paramMap)));
        } else {
            EgovMap resultMap = new EgovMap();
            resultMap.put("data", commonMapper.selectList(nsp.selectListQry(paramMap)));
            return resultMap;
        }
    }

    @Override
    public EgovMap getOne(int seq) throws Exception {
        return commonMapper.selectOne(nsp.selectOneQry(seq));
    }

    @Override
    public int insert(Map<String, Object> paramMap) throws Exception {
        return commonMapper.insert(nsp.insertQry(paramMap));
    }

    @Override
    public int update(Map<String, Object> paramMap) throws Exception {
        return commonMapper.update(nsp.updateQry(paramMap));
    }

    @Override
    public void delete(int seq) throws Exception {
        commonMapper.delete(nsp.deleteQry(seq));
    }

    @Override
    public Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Map<String, Object>> lists = objectMapper
                    .readValue(getClass().getClassLoader().getResourceAsStream("notice.json"),
                            new TypeReference<List<Map<String, Object>>>() {});

            return Paging.getPage(lists, pagingRequest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Page<>();
    }
}
