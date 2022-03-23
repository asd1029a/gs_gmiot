package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "t_user_group_in_user")
public class UserInGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idx")
    private int index;

    @Column(name = "insert_dt")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    private Timestamp insertDt;
    @Column(name = "insert_user_seq")
    private int insertUserSeq;


    @ManyToOne
    @JoinColumn(name = "user_seq")
    @JsonIgnore
    private User user;


    @ManyToOne
    @JoinColumn(name = "user_group_seq")
    private UserGroup userGroup;
}