package com.danusys.web.platform.service.notice;

import com.danusys.web.commons.app.PagingUtil;
import com.danusys.web.commons.app.StringUtil;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.repository.UserRepository;
import com.danusys.web.commons.auth.util.LoginInfoUtil;
import com.danusys.web.commons.app.EgovMap;
import com.danusys.web.platform.dto.request.NoticeRequestDto;
import com.danusys.web.platform.dto.response.NoticeResponseDto;
import com.danusys.web.platform.entity.Notice;
import com.danusys.web.platform.entity.NoticeSpecification;
import com.danusys.web.platform.entity.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 *
 * 클래스이름 : NoticeServiceImpl
 *
 * 작성자 : 강명훈 주임연구원
 * 작성일 : 2022-03-07
 * 설명 : 공지사항 비즈니스 로직 레이어 Impl
 *
**/

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public EgovMap getList(Map<String, Object> paramMap) throws Exception {
        EgovMap responseData = new EgovMap();
        List<User> userList = userRepository.findAll();

        /* 키워드 검색조건 */
        String keyword = paramMap.get("keyword").toString();
        Specification<Notice> spec = Specification.where(NoticeSpecification.likeContent(keyword))
                .or(NoticeSpecification.likeTitle(keyword));

        /* 날짜 검색조건 */
        Timestamp startDt = StringUtil.stringToTimestamp(paramMap.get("startDt").toString());
        Timestamp endDt = StringUtil.stringToTimestamp(paramMap.get("endDt").toString());
        if(startDt != null || endDt != null) {
            spec = spec.and(NoticeSpecification.betweenDateTime(startDt, endDt));
        }


        /* 데이터 테이블 리스트 조회*/
        if(paramMap.get("draw") != null) {
            /* 페이지 및 멀티소팅 */
            List<Sort.Order> orders = new ArrayList<>();
            Sort.Order order1 = new Sort.Order(Sort.Direction.DESC, "insertDt");
            orders.add(order1);
            Pageable pageable = PagingUtil.getPageableWithSort((int) paramMap.get("start"), (int) paramMap.get("length"), orders);

            Page<Notice> noticeList = noticeRepository.findAll(spec, pageable);
            List<NoticeResponseDto> data = noticeList.getContent().stream()
                    .map(notice -> {
                        String insertUserId = userList
                                .stream()
                                .filter(user -> notice.getInsertUserSeq() == user.getUserSeq())
                                .findFirst()
                                .orElse(null)
                                .getUserId();
                        AtomicReference<String> updateUserId = new AtomicReference<>(null);
                                userList.forEach(user -> {
                                    if(notice.getUpdateUserSeq() != null && notice.getUpdateUserSeq() == user.getUserSeq()) {
                                        updateUserId.set(user.getUserId());
                                    }
                                    }
                                );

                        return new NoticeResponseDto(notice, insertUserId, updateUserId.get());
                    })
                    .collect(Collectors.toList());

            responseData.put("data", data);
            responseData.put("recordsTotal", noticeList.getTotalElements());
            responseData.put("recordsFiltered", noticeList.getTotalElements());
            responseData.put("draw", (int) paramMap.get("draw"));
            responseData.put("start", (int) paramMap.get("start"));

        /* 일반 리스트 조회 */
        } else {
            List<Map<String, Object>> data = noticeRepository.findAll(spec).stream()
                    .map(notice -> {
                        ObjectMapper objectMapper = new ObjectMapper();
                        String insertUserId = userList
                                .stream()
                                .filter(user -> notice.getInsertUserSeq() == user.getUserSeq())
                                .findFirst()
                                .orElse(null)
                                .getUserId();
                        String updateUserId = userList
                                .stream()
                                .filter(user -> notice.getUpdateUserSeq() == user.getUserSeq())
                                .findFirst()
                                .orElse(null)
                                .getUserId();
                        NoticeResponseDto noticeResponseDto = new NoticeResponseDto(notice, insertUserId, updateUserId);
                        Map<String, Object> dataList = objectMapper.convertValue(noticeResponseDto, Map.class);

                        return dataList;
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

    /*
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
    */
}
