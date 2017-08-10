package io.swiftwallet.midgard.security.oauth;

import in.cfcomputing.odin.core.services.security.generator.UserGenerator;
import com.paytezz.commons.domain.security.WalletUser;
import com.paytezz.commons.persistence.cache.repository.user.WalletUserCache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service("userDetailsServiceBean")
public class MidgardUserDetailsService implements UserDetailsService {
    private final UserGenerator userGenerator;
    private final WalletUserCache walletUserCache;

    @Inject
    public MidgardUserDetailsService(final UserGenerator userGenerator, final WalletUserCache walletUserCache) {
        this.userGenerator = userGenerator;
        this.walletUserCache = walletUserCache;
    }

    @Override
    public UserDetails loadUserByUsername(final String userId) {
        final WalletUser walletUser;
        if (StringUtils.contains(userId, "@")) {
            walletUser = walletUserCache.findByEmail(userId);
        } else {
            walletUser = walletUserCache.findByMobileNumber(userId);
        }
        return userGenerator.generate(walletUser);
    }
}