package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@Table(name="t_user_group_in_user")
public class UserGroupInUser {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="idx")
        private int index;

        @Column(name="insert_dt")
        private Timestamp insertDt;
        @Column(name="insert_user_seq")
        private int insertUserSeq ;

        @Column(name="update_dt")
        private Timestamp updateDt;

        @Column(name="update_user_seq")
        private int updateUserSeq;

        //@OneToOne(fetch = FetchType.EAGER)
        // @ManyToOne
        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name ="user_seq")
        @JsonBackReference
        private User user;


        @OneToOne(fetch = FetchType.EAGER)
        @JoinColumn(name ="user_group_seq")
        @JsonManagedReference
        private UserGroup userGroup;



}
