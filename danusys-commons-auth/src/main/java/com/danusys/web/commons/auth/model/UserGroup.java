package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
//@EqualsAndHashCode
@Table(name="t_user_group")
public class UserGroup  implements Serializable {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="user_group_seq")
        private int userGroupSeq;


        @Column(name="user_group_name")
        private String groupName;
        @Column(name="user_group_remark")
        private String groupDesc;
        @Column(name="insert_dt")
        private Timestamp insertDt ;
        @Column(name="insert_user_seq")
        private int insertUserSeq ;
        @Column(name="update_dt")
        private Timestamp updateDt ;
        @Column(name="update_user_seq")
        private int updateUserSeq ;

        @OneToOne(mappedBy ="userGroup",fetch = FetchType.EAGER)
        @JsonBackReference
        private  UserGroupInUser userGroupInUser;

        @OneToMany(mappedBy ="userGroup2",fetch=FetchType.EAGER)
        @JsonManagedReference
        private final List<UserGroupPermit> userGroupPermit =new ArrayList<>();

}
