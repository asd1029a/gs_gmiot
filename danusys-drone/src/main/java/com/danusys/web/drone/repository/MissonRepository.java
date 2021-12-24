package com.danusys.web.drone.repository;

import com.danusys.web.drone.model.Misson;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MissonRepository extends CrudRepository<Misson,Long> {

    List<Misson> findByName(String name);

}
