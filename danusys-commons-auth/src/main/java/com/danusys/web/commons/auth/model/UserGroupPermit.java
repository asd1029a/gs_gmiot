package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="user_group_permit")
public class UserGroupPermit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="index")
        private int id;



      //  @Column(name="user_group_seq")
    //    private int userGroupSeq;


     //   @Column(name="permit_seq")
    //    private String permitSeq;
        @Column(name="insert_dt")
        private String insertDt ;
        @Column(name="insert_user_seq")
        private String insertUserSeq ;

        @OneToOne
        @JoinColumn(name ="user_group_seq")
        @JsonBackReference
        private UserGroup userGroup2;

        @OneToOne
        @JoinColumn(name ="permit_seq")
        @JsonManagedReference
        private Permit permit;

}
