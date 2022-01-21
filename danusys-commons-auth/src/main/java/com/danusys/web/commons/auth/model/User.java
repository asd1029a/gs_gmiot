package com.danusys.web.commons.auth.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name="t_user")
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name="id")
        private int id;


        @Column(name="user_id")
        private String username;

        private String password;

        @Column (name="refresh_token")
        private String refreshToken;

/*
        public List<String> getRoleList(){
                if(this.roles.length() >0){
                        return Arrays.asList(this.roles.split(","));
                }
                return new ArrayList<>();
        }
*/
        //@JsonIgnore
       // @OneToOne(mappedBy ="user",cascade =CascadeType.ALL,fetch = FetchType.EAGER)
        @OneToOne(mappedBy ="user")
        @JsonManagedReference
        private  UserGroupInUser userGroupInUser;





}
