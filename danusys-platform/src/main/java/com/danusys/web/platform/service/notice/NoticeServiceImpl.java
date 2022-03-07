package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.auth.mapper.common.CommonMapper;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.dto.request.NoticeRequestDto;
import com.danusys.web.platform.dto.response.NoticeResponseDto;
import com.danusys.web.platform.entity.Notice;
import com.danusys.web.platform.entity.NoticeSpecification;
import com.danusys.web.platform.mapper.notice.NoticeSqlProvider;
import com.danusys.web.platform.entity.NoticeRepository;
import com.danusys.web.platform.model.paging.Page;
import com.danusys.web.platform.model.paging.PagingRequest;
import com.danusys.web.platform.util.Paging;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @클래스이름 : NoticeServiceImpl
 *
 * @작성자 : 강명훈 주임연구원
 * @작성일 : 2022-03-07
 * @설명 : 공지사항 비즈니스 로직 레이어 Impl
 *
**/

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final CommonMapper commonMapper;
    private final NoticeSqlProvider nsp = new NoticeSqlProvider();
    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap responseData = new EgovMap();
        String startDt = paramMap.get("startDt").toString().concat(":00");
        String endDt = paramMap.get("endDt").toString().concat(":00");
        String keyword = paramMap.get("keyword").toString();

        /* 키워드 검색조건 */
        Specification<Notice> spec = Specification.where(NoticeSpecification.defaultWhere());
        spec = spec.or(NoticeSpecification.likeContent(keyword));
        spec = spec.or(NoticeSpecification.likeTitle(keyword));

        /* 날짜 검색조건 (util 공통기능으로 변경예정) */
        if(!":00".equals(startDt) && !":00".equals(endDt)
            && !"".equals(startDt) && !"".equals(endDt)) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime startLdt = LocalDateTime.from(dtf.parse(startDt));
            LocalDateTime endLdt = LocalDateTime.from(dtf.parse(endDt));
            Timestamp startDate = Timestamp.valueOf(startLdt);
            Timestamp endDate = Timestamp.valueOf(endLdt);
            spec = spec.and(NoticeSpecification.betweenDateTime(startDate, endDate));
        }

        /* 데이터 테이블 리스트 조회*/
        if(paramMap.get("draw") != null) {
            int start = (int) paramMap.get("start");
            int length = (int) paramMap.get("length");
            int page = start / length;

            Pageable pageable = PageRequest.of(page, length, Sort.by("insertDt").descending());
            org.springframework.data.domain.Page<Notice> noticeList = noticeRepository.findAll(spec, pageable);
            List<NoticeResponseDto> data = noticeList.getContent().stream()
                    .map(notice -> {
                        String insertUserId = userRepository.findByUserSeq(notice.getInsertUserSeq()).getUserId();
                        String updateUserId = "";
                        if(notice.getUpdateUserSeq() != null) {
                            updateUserId = userRepository.findByUserSeq(notice.getUpdateUserSeq()).getUserId();
                        }
                        return new NoticeResponseDto(notice, insertUserId, updateUserId);
                    })
                    .collect(Collectors.toList());

            responseData.put("data", data);
            responseData.put("recordsTotal", noticeList.getTotalElements());
            responseData.put("recordsFiltered", noticeList.getTotalElements());
            responseData.put("draw", (int) paramMap.get("draw"));
            responseData.put("start", start);

        /* 일반 리스트 조회 */
        } else {
            List<NoticeResponseDto> data = noticeRepository.findAll(spec).stream()
                    .map(notice -> {
                        String insertUserId = userRepository.findByUserSeq(notice.getInsertUserSeq()).getUserId();
                        String updateUserId = "";
                        if(notice.getUpdateUserSeq() != null) {
                            updateUserId = userRepository.findByUserSeq(notice.getUpdateUserSeq()).getUserId();
                        }
                        return new NoticeResponseDto(notice, insertUserId, updateUserId);
                    })
                    .collect(Collectors.toList());
            responseData.put("data", data);
        }
        return responseData;
    }

    @Override
    public NoticeResponseDto getOne(Long seq) throws Exception {
        Notice notice = noticeRepository.findByNoticeSeq(seq);
        String insertUserId = userRepository.findByUserSeq(notice.getInsertUserSeq()).getUserId();
        String updateUserId = "";
        if(notice.getUpdateUserSeq() != null) {
            updateUserId = userRepository.findByUserSeq(notice.getUpdateUserSeq()).getUserId();
        }
        return new NoticeResponseDto(notice, insertUserId, updateUserId);
    }

    @Override
    @Transactional
    public void add(NoticeRequestDto noticeRequestDto) throws Exception {
        noticeRequestDto.setInsertUserSeq(LoginInfoUtil.getUserDetails().getUserSeq());
        noticeRepository.save(noticeRequestDto.toEntity());
    }

    @Override
    @Transactional
    public void mod(Long seq, NoticeRequestDto noticeRequestDto) throws Exception {
        Notice notice = noticeRepository.findByNoticeSeq(seq);
        notice.update(noticeRequestDto.getNoticeTitle()
                , noticeRequestDto.getNoticeContent()
                , noticeRequestDto.getNoticeFile());
    }

    @Override
    public void del(Long seq) throws Exception {
        noticeRepository.deleteById(seq);
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
