package io.swiftwallet.midgard.security.oauth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("userDetailsServiceBean")
public class MidgardUserDetailsService implements UserDetailsService {

    @Inject
    private UserGenerator userGenerator;

    @Override
    public UserDetails loadUserByUsername(final String userName){
        return userGenerator.generateUser(userName);
    }
}