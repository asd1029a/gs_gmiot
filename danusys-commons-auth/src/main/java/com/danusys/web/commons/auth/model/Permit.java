package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
@Table(name="t_permit")
public class Permit {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="permit_seq")
        private int permitSeq;
        @Column(name="permit_name")
        private String permitName;
        @Column(name="insert_dt")
        private Date insertDt ;
        @Column(name="insert_user_seq")
        private int insertUserSeq ;


        @OneToOne(mappedBy ="permit")
        @JsonBackReference
        private  UserGroupPermit userGroupPermit;
}
