package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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

    @Column(name = "insert_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    @Column(name = "insert_user_seq")
    private int insertUserSeq;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_group_seq")
    @JsonBackReference
    private UserGroup userGroup2;

    @OneToOne
    @JoinColumn(name="permit_seq", referencedColumnName="code_seq")
    private Permit permit;

    @OneToOne
    @JoinColumn(name="permit_menu_seq", referencedColumnName="code_seq")
    private PermitMenu permitManu;
}