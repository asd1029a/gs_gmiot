package com.danusys.web.platform.controller;


import com.danusys.web.platform.service.board.BoardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BoardController {

    private BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @GetMapping("/boards")
    public Map<String, Object> boards(@RequestBody Map<String, Object> param,
                                      @PageableDefault(size=10, sort="id", direction=Sort.Direction.DESC) Pageable pageable) throws  Exception {
        Map<String,Object> result = new HashMap<>();
        Page<Map<String, Object>> boards = boardService.selectListBoard(param, pageable);
        result.put("contents", boards);
        result.put("size", pageable.getPageSize());
        return result;
    }

}
