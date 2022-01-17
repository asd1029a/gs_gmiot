package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Data
@Table(name="user_group_in_user")
public class UserGroupInUser {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="index")
        private int index;

        @Column(name="insert_dt")
        private Date insertDt;
        @Column(name="insert_user_seq")
        private int insertUserSeq ;


        //@OneToOne(fetch = FetchType.EAGER)
        // @ManyToOne
        @OneToOne
        @JoinColumn(name ="user_seq")
        @JsonBackReference
        private User user;

        @OneToOne
        @JoinColumn(name ="user_group_seq")
        @JsonManagedReference
        private UserGroup userGroup;



}
