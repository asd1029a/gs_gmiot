package com.danusys.web.commons.auth.config.auth;


import com.danusys.web.commons.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j

public class CommonsUserDetails implements UserDetails {

    private User user;
    // private Permit permit;
    private List<String> permitList;

    public CommonsUserDetails(User user) {
        this.user = user;

        //   this.permit = permit;
        permitList = new ArrayList<>();
    }

    public User getUser() {
        return user;
    }

    @Override

    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {

        return user.getUserId();
    }

    public int getUserSeq() {

        return user.getUserSeq();
    }


    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        //  log.info("here={}",user.getUserGroupInUser());

        if (user == null) {


        } else {
            //permitList.add(user.getUserGroupInUser().getUserGroup().getUserGroupPermit().getPermit().getPermitName());
            user.getUserGroupInUser().forEach(r -> {
                r.getUserGroup().getUserGroupPermit().forEach(rr -> {
                    permitList.add(rr.getPermit().getPermitName());
                });
            });
            permitList.forEach(r -> {
                authorities.add(() -> {
                    return r;
                });
            });
        }

        return authorities;
    }


    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }


}