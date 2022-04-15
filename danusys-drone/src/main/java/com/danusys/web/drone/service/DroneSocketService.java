package com.danusys.web.drone.service;

import com.danusys.web.drone.model.DroneSocket;
import com.danusys.web.drone.repository.DroneSocketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DroneSocketService {

    private final DroneSocketRepository droneSocketRepository;

    public List<DroneSocket> getList(){
        return droneSocketRepository.findAll();
    }
    @Transactional
    public void saveList(DroneSocket droneSocket){
        droneSocketRepository.save(droneSocket);
        //droneSocketRepository.saveAll();
    }
    @Transactional
    public void delete() {
        droneSocketRepository.deleteAllInBatch();
    }
}
