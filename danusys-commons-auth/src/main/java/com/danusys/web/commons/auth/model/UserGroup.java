package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Table(name="t_user_group")
public class UserGroup {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="user_group_seq")
        private int userGroupSeq;


        @Column(name="user_group_name")
        private String groupName;
        @Column(name="user_group_remark")
        private String groupDesc;
        @Column(name="insert_dt")
        private Date insertDt ;
        @Column(name="insert_user_seq")
        private int insertUserSeq ;
        @Column(name="update_dt")
        private Date updateDt ;
        @Column(name="update_user_seq")
        private int updateUserSeq ;

        @OneToOne(mappedBy ="userGroup")
        @JsonBackReference
        private  UserGroupInUser userGroupInUser;

        @OneToOne(mappedBy ="userGroup2")
        @JsonManagedReference
        private  UserGroupPermit userGroupPermit;

}
