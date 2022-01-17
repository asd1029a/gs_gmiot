package com.danusys.web.commons.auth.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor

public class UserDto {

        private int id;
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
