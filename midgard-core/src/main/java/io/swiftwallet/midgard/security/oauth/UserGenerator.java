package io.swiftwallet.midgard.security.oauth;

import io.swiftwallet.common.domain.security.AuthenticatedUser;
import io.swiftwallet.common.domain.security.RoleType;
import io.swiftwallet.common.domain.security.WalletUser;
import io.swiftwallet.common.persistence.cache.repository.user.WalletUserCache;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Service
public class UserGenerator {

    @Inject
    public UserGenerator(final WalletUserCache walletUserCache) {
        this.walletUserCache = walletUserCache;
    }

    private final WalletUserCache walletUserCache;

    public User generateUser(final String userId) {
        final WalletUser walletUser;
        if (StringUtils.contains(userId, "@")) {
            walletUser = walletUserCache.findByEmail(userId);
        } else {
            walletUser = walletUserCache.findOne(StringUtils.replace(userId, " ", "+"));
        }

        Validate.notNull(walletUser, "User with id [%s] not found.", userId);

        final Set<GrantedAuthority> authorities = new HashSet<>();

        final RoleType roleType = walletUser.getRole();
        if (roleType != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleType.name()));
        }
        final AuthenticatedUser authenticatedUser = new AuthenticatedUser(userId, walletUser, authorities);
        return authenticatedUser;
    }
}