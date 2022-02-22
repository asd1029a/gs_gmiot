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
import java.util.Map;

public interface UserRepository extends JpaRepository<User,Long> , JpaSpecificationExecutor<User> {

    User findByUserId(String username);


    @Query("SELECT u,us.codeName FROM User u left join UserStatus us ON u.status = us.codeValue")
    List<User> getList();
    User findByUserSeq(int userSeq);

    Long deleteByUserSeq(int id);

    List<User> findAll();

    Page<User> findAll(Pageable pageable);


    Page<User> findAll(Specification<User> spec,Pageable pageable);
    Page<User> findAllByUserNameLike(String userName,Pageable pageable);
    List<User> findAllByUserNameLike(String userName);



}
