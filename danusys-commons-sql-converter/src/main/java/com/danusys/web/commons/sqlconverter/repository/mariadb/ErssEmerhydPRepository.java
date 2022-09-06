package com.danusys.web.commons.sqlconverter.repository.mariadb;

import com.danusys.web.commons.sqlconverter.model.mariadb.ErssEmerhydP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ErssEmerhydPRepository extends JpaRepository<ErssEmerhydP, Long> {
    //Optional<ErssEmerhydP> findById(Long s);

//    List<ErssEmerhydP> findAll();
}
