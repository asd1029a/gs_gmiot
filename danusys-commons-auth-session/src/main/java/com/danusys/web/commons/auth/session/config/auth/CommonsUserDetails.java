package com.danusys.web.commons.auth.session.config.auth;


import com.danusys.web.commons.auth.session.model.User;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Data
public class CommonsUserDetails implements UserDetails {

    private User user;
    // private Permit permit;
    private Set<String> permitList;


    public CommonsUserDetails(User user) {
        this.user = user;
        permitList = new LinkedHashSet<String>();
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
    //

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
            System.out.println("여기들어옴");
            user.getUserInGroup().forEach(userInGroup -> {
                userInGroup.getUserGroup()
                        .getUserGroupPermit()
                        .stream()
                        .filter(userGroupPermit -> !permitList.add("ROLE_" + userGroupPermit.getPermitMenu().getCodeValue() + "_" + userGroupPermit.getPermit().getCodeValue()))
                        .collect(Collectors.toList());
            });
            System.out.println("permitlist!! : " + permitList);
            permitList.forEach(permit -> {
                authorities.add(new SimpleGrantedAuthority(permit));
            });
        }

        return authorities;
    }









/*
    @Override
    @Transactional
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
*/
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
        }*//*



        permitList.add("ROLE_MANAGER");

        permitList.forEach(permit -> {
            authorities.add(new SimpleGrantedAuthority(permit));
        });

        return authorities;
    }
*/

}