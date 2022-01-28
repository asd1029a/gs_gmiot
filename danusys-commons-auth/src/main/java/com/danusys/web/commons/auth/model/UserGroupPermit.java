package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Data
@Table(name="t_user_group_permit")
public class UserGroupPermit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="idx")
        private int id;



      //  @Column(name="user_group_seq")
    //    private int userGroupSeq;


     //   @Column(name="permit_seq")
    //    private String permitSeq;
        @Column(name="insert_dt")
        private Date insertDt ;
        @Column(name="insert_user_seq")
        private int insertUserSeq ;

        @OneToOne
        @JoinColumn(name ="user_group_seq")
        @JsonBackReference
        private UserGroup userGroup2;

        @OneToOne
        @JoinColumn(name ="permit_seq")
        @JsonManagedReference
        private Permit permit;

}
