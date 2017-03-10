package io.swiftwallet.midgard.common.user;

import io.swiftwallet.common.domain.security.WalletUser;
import io.swiftwallet.common.persistence.cache.repository.user.WalletUserCache;
import io.swiftwallet.common.util.security.provider.AuthenticatedUserProvider;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class CurrentUserFacade {

    @Inject
    private AuthenticatedUserProvider userProvider;
    @Inject
    private WalletUserCache walletUserCache;

    public WalletUser retrieveCurrentUser() {
        final User user = userProvider.user();
        return walletUserCache.findOne(user.getUsername());
    }
}
