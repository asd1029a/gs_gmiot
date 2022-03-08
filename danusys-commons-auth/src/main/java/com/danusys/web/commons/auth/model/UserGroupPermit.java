package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "t_user_group_permit")
public class UserGroupPermit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private int id;

    //  @Column(name="user_group_seq")
    //    private int userGroupSeq;

    //   @Column(name="permit_seq")
    //    private String permitSeq;
    @Column(name = "insert_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    @Column(name = "insert_user_seq")
    private int insertUserSeq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_group_seq")
    @JsonBackReference
    private UserGroup userGroup2;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permit_seq")
    @JsonManagedReference
    private Permit permit;
}