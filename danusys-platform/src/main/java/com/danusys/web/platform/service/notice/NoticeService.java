package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.app.EgovMap;
import java.util.List;

import com.danusys.web.platform.dto.request.NoticeRequestDto;
import com.danusys.web.platform.dto.response.NoticeResponseDto;
import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;

import java.util.Map;

/**
 *
 * @클래스이름 : NoticeService
 *
 * @작성자 : 강명훈 주임연구원
 * @작성일 : 2022-03-07
 * @설명 : 공지사항 비즈니스 로직 interface
 *
**/

public interface NoticeService {
    EgovMap getList(Map<String, Object> paramMap) throws Exception;
    NoticeResponseDto getOne(Long seq) throws Exception;
    void add(NoticeRequestDto noticeRequestDto) throws Exception;
    void mod(Long seq, NoticeRequestDto noticeRequestDto) throws Exception;
    void del(Long seq) throws Exception;

    Page<List<Map<String, Object>>> getLists(PagingRequest pagingRequest);
}
