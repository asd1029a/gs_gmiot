package com.danusys.web.drone.repository;

        import com.danusys.web.drone.model.Drone;
        import com.danusys.web.drone.model.Mission;
        import org.springframework.data.domain.Sort;
        import org.springframework.data.repository.CrudRepository;

        import java.util.List;
        import java.util.Map;


public interface MissionRepository extends CrudRepository<Mission,Long> {

        List<Mission> findAllByNameLike(String name, Sort sort);


        List<Mission> findAllById(Long id);

        Mission findByName(String name);

        List<Mission> findAllByUserIdLike(String adminUserId,Sort sort);

        List<Mission> findAllByDrone(Drone drone);

        List<Mission> findByDroneNot(Drone drone);



}
