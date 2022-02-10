package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "t_permit")
public class Permit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permit_seq")
    private int permitSeq;
    @Column(name = "permit_name")
    private String permitName;
    @Column(name = "insert_dt")
    private Timestamp insertDt;
    @Column(name = "insert_user_seq")
    private int insertUserSeq;

    @Column(name = "update_dt")
    private Timestamp updateDt;
    @Column(name = "update_user_seq")
    private int updateUserSeq;

    @OneToOne(mappedBy = "permit", fetch = FetchType.EAGER)
    @JsonBackReference
    private UserGroupPermit userGroupPermit;
}
