package com.danusys.web.commons.auth.service.repository;


import com.danusys.web.commons.auth.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {
    User findByUserId(String id);
//    List<User> getList();
//    @Query(value = "select * from t_user where user_seq = :userSeq", nativeQuery = true)
    User findByUserSeq(int userSeq);

    Long deleteByUserSeq(int id);
    List<User> findAll();
    Page<User> findAll(Pageable pageable);
    Page<User> findAll(Specification<User> spec, Pageable pageable);
    Page<User> findAllByUserNameLike(String userName,Pageable pageable);
    List<User> findAllByUserNameLike(String userName);
}
