package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Drone;
import com.danusys.web.drone.model.Mission;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface DroneRepository extends CrudRepository<Drone, Long> {


    void deleteById(Long id);


    List<Drone> findAllById(Long id);

    List<Drone> findAllByUserIdLike(String userId, Sort sort);

    List<Drone> findAllByDroneDeviceNameLike(String droneDeviceName, Sort sort);

    Drone findByDroneDeviceName(String droneDeviceName);


    List<Drone> findAllByIdNot(long id);

//
//    List<Mission> findAllByNameLikeAndDrone(String name, Drone drone, Sort sort);
//
//    List<Mission> findAllByNameLikeAndDroneNot(String name, Drone drone, Sort sort);
//
//    List<Mission> findAllByUserIdLikeAndDrone(String adminUserId, Drone drone, Sort sort);
//
//    List<Mission> findAllByUserIdLikeAndDroneNot(String adminUserId, Drone drone, Sort sort);


    @Query(value = "select * from drone", nativeQuery = true)
    List<Map<String, Object>> test();

    @Query(value = "SELECT m.name,md.index FROM MISSION m INNER JOIN MISSION_DETAILS md " +
            "ON(m.id = md.MISSION_ID )  WHERE m.id = 40 ORDER BY md.INDEX ", nativeQuery = true)
    List<Map<String, Object>> test2();


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