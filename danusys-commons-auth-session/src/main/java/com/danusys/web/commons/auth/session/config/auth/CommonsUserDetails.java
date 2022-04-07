package com.danusys.web.commons.auth.session.config.auth;


import com.danusys.web.commons.auth.session.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@Data
public class CommonsUserDetails implements UserDetails {

    private User user;
    // private Permit permit;
    private List<String> permitList;


    public CommonsUserDetails(User user) {

        this.user = user;

        /*   permitList = new ArrayList<>();*/
    }

    public String getUserId() {
        return user.getUserId();
    }

    public int getUserSeq() {
        return user.getUserSeq();
    }


    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
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


    /*    */

    /**
     * 계정의 권한 목록 리턴
     *
     * @return
     *//*
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collectors = new ArrayList<>();
        collectors.add(() -> String.valueOf(user.getRole()));

        return collectors;
    }*/
    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
/*

        if (user != null) {
            // TODO - 게시판 별 권한 처리 협의 필요 @엄태혁
            user.getUserInGroup().forEach(r -> {
                r.getUserGroup().getUserGroupPermit().forEach(rr -> {
                    log.info("aaaaa" + rr.getPermit().getCodeValue());
                    permitList.add(rr.getPermit().getCodeValue());
                });
            });

            permitList.forEach(r -> {
                authorities.add(() -> {
                    return r;
                });
            });
        }*/
        return authorities;
    }

}