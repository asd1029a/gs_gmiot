package com.danusys.web.commons.auth.config.auth;


import com.danusys.web.commons.auth.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j

public class CommonsUserDetails implements UserDetails {

    private User user;
    // private Permit permit;
    private Set<String> permitList;

    public CommonsUserDetails(User user) {
        this.user = user;
        permitList = new LinkedHashSet<String>();
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

    public String getUserId() {   return user.getUserId(); }

    public int getUserSeq() {
        return user.getUserSeq();
    }


    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

//        if (user != null) {
//            user.getUserInGroup().forEach(userInGroup -> {
//                userInGroup.getUserGroup()
//                        .getUserGroupPermit()
//                        .forEach(groupPermit -> {
//                    permitList.add("ROLE_" + groupPermit.getPermitMenu().getCodeValue() + "_" + groupPermit.getPermit().getCodeValue());
//                });
//            });
//            permitList.forEach(r -> {
//                authorities.add(new SimpleGrantedAuthority(r));
//            });
//        }
        if (user != null) {
            user.getUserInGroup().forEach(userInGroup -> {
                userInGroup.getUserGroup()
                        .getUserGroupPermit()
                        .stream()
                        .filter(userGroupPermit -> !permitList.add("ROLE_" + userGroupPermit.getPermitMenu().getCodeValue() + "_" + userGroupPermit.getPermit().getCodeValue()))
                        .collect(Collectors.toList());
            });
            permitList.forEach(permit -> {
                authorities.add(new SimpleGrantedAuthority(permit));
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