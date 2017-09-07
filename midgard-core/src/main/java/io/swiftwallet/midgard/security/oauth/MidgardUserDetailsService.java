package io.swiftwallet.midgard.security.oauth;

import com.paytezz.commons.util.security.generator.WalletUserGenerator;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("userDetailsServiceBean")
public class MidgardUserDetailsService implements UserDetailsService {
    private final WalletUserGenerator userGenerator;

    @Inject
    public MidgardUserDetailsService(final WalletUserGenerator userGenerator) {
        this.userGenerator = userGenerator;
    }

    @Override
    public UserDetails loadUserByUsername(final String userId) {
        return userGenerator.generateUserDetails(userId);
    }
}