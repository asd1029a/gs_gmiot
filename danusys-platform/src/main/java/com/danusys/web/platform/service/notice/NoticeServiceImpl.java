package com.danusys.web.platform.service.notice;

import com.danusys.web.platform.mapper.NoticeMapper;
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

    public NoticeServiceImpl(NoticeMapper noticeMapper) {this.noticeMapper = noticeMapper;}

    private final NoticeMapper noticeMapper;

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, noticeMapper.selectList(paramMap));
    }

    @Override
    public Map<String, Object> getOne(int noticeSeq) throws Exception {
        return noticeMapper.selectOne(noticeSeq);
    }
//    @Override
//    public Page<Map<String, Object>> selectListNotice(Map<String, Object> param, Pageable pageable) throws Exception {
//        param.put("offset", pageable.getOffset());
//        param.put("pageSize", pageable.getPageSize());
//
//        List<HashMap<String, Object>> list = noticeMapper.selectAll(param);
//        long count = list.stream().count();
//
//        return new PageImpl(list, pageable, count);
//    }

    @Override
    public String insertNotice(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public String updateNotice(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public String deleteNotice(Map<String, Object> paramMap) throws Exception {
        return null;
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
