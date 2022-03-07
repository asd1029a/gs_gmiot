package com.danusys.web.drone.service;


import com.danusys.web.drone.repository.DroneRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TestService {

    private final DroneRepository droneRepository;

    public List<Map<String,Object>> Test(){

        return droneRepository.test();

    }

    public List<Map<String,Object>> Test2(){

        return droneRepository.test2();

    }


}
