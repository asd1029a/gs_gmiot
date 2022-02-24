package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.DroneLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

public interface DroneLogRepository extends CrudRepository<DroneLog, Long> {


    DroneLog findById(String missionName);

    List<DroneLog> findAll();

    Page<DroneLog> findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeAndMissionNameIgnoreCaseLike(Date beforeDate, Date afterDate, String droneDeviceName, String missionName, Pageable pageable);

    Page<DroneLog> findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeOrInsertDtBetweenAndMissionNameIgnoreCaseLike(Date beforeDate, Date afterDate, String droneDeviceName,Date beforeDate2, Date afterDate2, String missionName, Pageable pageable);

    List<DroneLog> findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeAndMissionNameIgnoreCaseLike(Date beforeDate, Date afterDate,  String droneDeviceName, String missionName);

    List<DroneLog> findByInsertDtBetweenAndDroneDeviceNameIgnoreCaseLikeOrInsertDtBetweenAndMissionNameIgnoreCaseLike(Date beforeDate, Date afterDate, String droneDeviceName,Date beforeDate2, Date afterDate2, String missionName);
}
