package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
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
@Table(name = "t_user_group")
public class UserGroup implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_group_seq")
    private int userGroupSeq;


    @Column(name = "user_group_name")
    private String groupName;
    @Column(name = "user_group_remark")
    private String groupDesc;
    @Column(name = "insert_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    @Column(name = "insert_user_seq")
    private int insertUserSeq;
    @Column(name = "update_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp updateDt;
    @Column(name = "update_user_seq")
    private int updateUserSeq;

    @OneToMany(mappedBy = "userGroup", fetch = FetchType.LAZY)
    @JsonBackReference
    private final List<UserGroupInUser> userGroupInUser =new ArrayList<>();

    @OneToMany(mappedBy = "userGroup2", fetch = FetchType.EAGER)
    @JsonManagedReference
    private final List<UserGroupPermit> userGroupPermit = new ArrayList<>();

}
