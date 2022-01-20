package com.danusys.web.platform.service.board;

import com.danusys.web.platform.mapper.BoardMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    public BoardServiceImpl(BoardMapper boardMapper) {this.boardMapper = boardMapper;}

    private final BoardMapper boardMapper;

    @Override
    public List<Map<String, Object>> selectListBoard(Map<String, Object> paramMap) throws Exception {
        return boardMapper.selectAll();
    }

    @Override
    public Page<Map<String, Object>> selectListBoard(Map<String, Object> param, Pageable pageable) throws Exception {
        param.put("offset", pageable.getOffset());
        param.put("pageSize", pageable.getPageSize());

        List<HashMap<String, Object>> list = boardMapper.selectAll(param);
        long count = list.stream().count();

        return new PageImpl(list, pageable, count);
    }

    @Override
    public String insertBoard(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public String updateBoard(Map<String, Object> paramMap) throws Exception {
        return null;
    }

    @Override
    public String deleteBoard(Map<String, Object> paramMap) throws Exception {
        return null;
    }
}
