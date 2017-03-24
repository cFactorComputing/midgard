package io.swiftwallet.midgard.common.auth.command;

import io.swiftwallet.commons.domain.security.AuthenticatedUser;
import io.swiftwallet.commons.persistence.cache.repository.security.AuthenticatedUserCache;
import io.swiftwallet.commons.util.security.provider.AuthenticatedUserProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class AuthenticatedUserFacade {
    private final AuthenticatedUserProvider userProvider;
    private final AuthenticatedUserCache authenticatedUserCache;
    private final TokenStore tokenStore;

    @Inject
    public AuthenticatedUserFacade(final AuthenticatedUserProvider userProvider,
                                   final AuthenticatedUserCache authenticatedUserCache,
                                   final TokenStore tokenStore) {
        this.userProvider = userProvider;
        this.authenticatedUserCache = authenticatedUserCache;
        this.tokenStore = tokenStore;
    }

    public void logout(final String accessToken) {
        authenticatedUserCache.delete(accessToken);
        final String userId = userProvider.userId();
        final Iterable<AuthenticatedUser> users = authenticatedUserCache.findByUserId(userId);
        authenticatedUserCache.delete(users);

        final OAuth2AccessToken oauth2Token = tokenStore.readAccessToken(accessToken);
        if (oauth2Token != null) {
            tokenStore.removeAccessToken(oauth2Token);
            tokenStore.removeRefreshToken(oauth2Token.getRefreshToken());
        }
    }
}