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

        //권한에 대한 security 규픽이 role_권한명 이렇게되있어서 permitlist에 등록을하고...
        //그걸 authorites에 넣는거긴한데.. 그럼 이 권한manager를 어디서 ? 찾아오는지?? 디비?? 없는데??
        //지금  아래는 그냥 막셋팅한거고 잠시만요결국엔 또 권한이 문젠가..??
        //아이거어떻게하지..
        //지금 model 구조에서는stackoverflow가날수박에없는데 ..
        //stackoverflow 에러나면 무조건 error 페이지로 넘어가게되있나봐요 ...
        // 아하..


        permitList.add("ROLE_MANAGER");

        permitList.forEach(permit -> {
            authorities.add(new SimpleGrantedAuthority(permit));
        });

        return authorities;
    }

}