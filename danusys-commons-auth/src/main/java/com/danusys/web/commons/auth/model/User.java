package com.danusys.web.commons.auth.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private int userSeq;


    @Column(name = "id")
    private String userId;
    //만약에 에러가 날경우 userId-> username auth 쪽에있는 get set userId username으로 바꿀껏
    //repository도수정햇음


    private String password;

    @Column(name = "user_name")
    private String userName;
    //위에 문제로 바꿨을경우 이것도 수정할것

    private String email;

    private String tel;

    private String address;


    @Column(name = "detail_address")
    private String detailAddress;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "last_login_dt")
    private Timestamp lastLoginDt;

    @Column(name = "insert_user_seq")
    private int insertUserSeq;

    @Column(name = "update_user_seq")
    private int updateUserSeq;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "insert_dt")
    private Timestamp insertDt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd kk:mm:ss", timezone = "Asia/Seoul")
    @Column(name = "update_dt")
    private Timestamp updateDt;

    @Column(name = "refresh_token")
    private String refreshToken;

    private String status;

    /*
    public List<String> getRoleList(){
            if(this.roles.length() >0){
                    return Arrays.asList(this.roles.split(","));
            }
            return new ArrayList<>();
    }
    */

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<UserInGroup> userInGroup =new ArrayList<>();
}
