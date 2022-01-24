package com.danusys.web.platform.service.notice;

import com.danusys.web.platform.mapper.NoticeMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    public NoticeServiceImpl(NoticeMapper noticeMapper) {this.noticeMapper = noticeMapper;}

    private final NoticeMapper noticeMapper;

    @Override
    public Map<String, Object> selectListNotice(Map<String, Object> paramMap) throws Exception {
        System.out.println(noticeMapper.selectAll(paramMap));
        return PagingUtil.createPagingMap(paramMap, noticeMapper.selectAll(paramMap));
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
}
