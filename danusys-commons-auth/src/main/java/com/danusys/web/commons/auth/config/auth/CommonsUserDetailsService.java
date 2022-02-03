package com.danusys.web.commons.auth.config.auth;



import com.danusys.web.commons.auth.model.Permit;
import com.danusys.web.commons.auth.model.User;
import com.danusys.web.commons.auth.model.UserDto;
import com.danusys.web.commons.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("loadUserByUsername, id={}",username);

        User user =userRepository.findByUsername(username);
        log.info("End loadUserByUsername{}");
//
//       user.getUserGroupInUser().getUserGroup().getUserGroupPermit().forEach(r->{
//
//           log.info("r={}",r.getPermit().getPermitName());
//       });

        if(user==null){
            return null;
        }
        return new CommonsUserDetails(user);
    }

}

