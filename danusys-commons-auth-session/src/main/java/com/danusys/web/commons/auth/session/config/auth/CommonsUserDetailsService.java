package com.danusys.web.commons.auth.session.config.auth;
import com.danusys.web.commons.auth.session.model.User;
import com.danusys.web.commons.auth.session.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j

public class CommonsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public CommonsUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(username);
        log.info("### id, username, pwd : {}, {}, {}", user.getUserId(), user.getUserName(), user.getPassword());

        if(user==null){
            throw new UsernameNotFoundException("해당 아이디가 존재하지 않습니다");
        }
        return new CommonsUserDetails(user);
    }

}

