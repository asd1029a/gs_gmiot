package com.danusys.web.commons.auth.config.auth;



import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommonsUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername, id={}",username);

        User user =userRepository.findByUsername(username);
        log.info("End loadUserByUsername{}");
      //  Permit permit=user.getUserGroupInUser().getUserGroup().getUserGroupPermit().getPermit();
       //log.info("permit={}",permit);
        if(user==null){
            return null;
        }
        return new CommonsUserDetails(user);
    }

}

