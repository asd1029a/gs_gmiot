package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {

        private int id;



        private String username;
        //만약에 에러가 날경우 userId-> username auth 쪽에있는 get set userId username으로 바꿀껏
        //repository도수정햇음


        private String password;


        private String userName2;
        //위에 문제로 바꿨을경우 이것도 수정할것

        private String email;

        private String tel;

        private String address;

        private char status;


        private String detailAddress;

        private Timestamp lastLoginDt;

        private int insertUserSeq;

        private int updateUserSeq;

        private Timestamp insertDt;

        private Timestamp updateDt;

        private String refreshToken;



        private  UserGroupInUser userGroupInUser;


}
