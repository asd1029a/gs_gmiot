package com.danusys.web.commons.auth.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Table(name="admin_test")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        private String username;
        private String password;
        private String roles;
        private String refreshToken;

        public List<String> getRoleList(){
                if(this.roles.length() >0){
                        return Arrays.asList(this.roles.split(","));
                }
                return new ArrayList<>();
        }

}
