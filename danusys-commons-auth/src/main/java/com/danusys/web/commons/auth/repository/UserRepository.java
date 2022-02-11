package com.danusys.web.commons.auth.repository;


import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.model.UserGroupInUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;


import java.util.List;

public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {

    User findByUserId(String username);


//    @Query("SELECT u FROM User u WHERE u.status = :status and u.name = :name")
//    USerDto
    User findByUserSeq(int userSeq);

    Long deleteByUserSeq(int id);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);

    Page<User> findAll(Specification<User> spec,Pageable pageable);
    Page<User> findAllByUserNameLike(String userName,Pageable pageable);
    List<User> findAllByUserNameLike(String userName);
}
