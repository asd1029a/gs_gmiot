package com.danusys.web.drone.repository;

        import com.danusys.web.drone.model.Mission;
        import org.springframework.data.repository.CrudRepository;

        import java.util.List;

public interface MissionRepository extends CrudRepository<Mission,Long> {

        List<Mission> findAllByName(String name);




}
