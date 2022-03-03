package com.danusys.web.drone.service;


import com.danusys.web.drone.model.DroneBase;
import com.danusys.web.drone.repository.DroneBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class DroneBaseService {

    private final DroneBaseRepository droneBaseRepository;


    public List<DroneBase> findAllDroneBase(){
        return droneBaseRepository.findAll();
    }





}
