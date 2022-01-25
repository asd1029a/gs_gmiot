package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.util.EgovMap;
import com.danusys.web.platform.mapper.NoticeMapper;
import com.danusys.web.platform.util.PagingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NoticeServiceImpl implements NoticeService {

    public NoticeServiceImpl(NoticeMapper noticeMapper) {this.noticeMapper = noticeMapper;}

    private final NoticeMapper noticeMapper;

    @Override
    public Map<String, Object> getList(Map<String, Object> paramMap) throws Exception {
        return PagingUtil.createPagingMap(paramMap, noticeMapper.selectList(paramMap));
    }

    public EgovMap getOne(int noticeSeq) throws Exception {
        return noticeMapper.selectOne(noticeSeq);
    }

    @Override
    public String insert(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public String update(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public String delete(Map<String, Object> paramMap) throws Exception {
        return null;
    }
}
