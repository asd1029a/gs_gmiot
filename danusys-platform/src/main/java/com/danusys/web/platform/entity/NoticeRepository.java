package com.danusys.web.platform.entity;

import com.danusys.web.platform.dto.request.NoticeRequestDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 *
 * @클래스이름 : NoticeRepository
 *
 * @작성자 : 강명훈 주임연구원
 * @작성일 : 2022-03-07
 * @설명 : 공지사항 Repository 클래스
 *
**/

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice> {

    List<Notice> findAll();
    Notice findByNoticeSeq(Long seq);
}
