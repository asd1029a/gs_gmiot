package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface DroneRepository extends CrudRepository<Drone,Long> {



    void deleteById(Long id);


    List<Drone> findAllById(Long id);

    List<Drone> findAllByDroneDeviceNameLike(String droneDeviceName, Sort sort);

    Drone findByDroneDeviceName(String droneDeviceName);


    List<Drone> findAllByIdNot(long id);
}
//@Query(value ="SELECT *\n" +
//            "FROM\n" +
//            "    drone d\n" +
//            "        INNER JOIN(\n" +
//            "        SELECT *\n" +
//            "        FROM\n" +
//            "            MISSION m\n" +
//            "                INNER JOIN\n" +
//            "            MISSION_DETAILS md\n" +
//            "            ON(m.id = md.MISSION_ID )\n" +
//            "--         WHERE m.id = 40\n" +
//            "        ORDER BY md.INDEX\n" +
//            ") a2\n" +
//            "ON (d.id= a2.drone_id)\n" +
//            "order by a2.index\n" ,nativeQuery = true)