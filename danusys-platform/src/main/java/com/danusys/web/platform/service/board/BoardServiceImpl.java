package com.danusys.web.platform.service.board;

import com.danusys.web.platform.mapper.BoardMapper;
import org.springframework.stereotype.Service;

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
