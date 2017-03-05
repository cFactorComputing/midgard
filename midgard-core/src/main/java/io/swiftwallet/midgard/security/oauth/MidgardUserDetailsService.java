package io.swiftwallet.midgard.security.oauth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("userDetailsServiceBean")
public class MidgardUserDetailsService implements UserDetailsService {
    private final UserGenerator userGenerator;

    @Inject
    public MidgardUserDetailsService(final UserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    @Override
    public UserDetails loadUserByUsername(final String userName) {
        return userGenerator.generateUser(userName);
    }
}