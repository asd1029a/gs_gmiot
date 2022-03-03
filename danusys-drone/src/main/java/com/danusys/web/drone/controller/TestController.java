package com.danusys.web.drone.controller;

import com.danusys.web.drone.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Controller
@RestController
@RequiredArgsConstructor
public class TestController {


    private final TestService testService;

    @GetMapping("/nativeTest")
    public ResponseEntity<?> nattiveTest(){


        return ResponseEntity.status(HttpStatus.OK).body(testService.Test());

    }


    @GetMapping("/nativeTest2")
    public ResponseEntity<?> nattiveTest2(){


        return ResponseEntity.status(HttpStatus.OK).body(testService.Test2());

    }




}
