package io.swiftwallet.midgard.common.auth.command;

import io.swiftwallet.common.persistence.cache.repository.security.AuthenticatedUserCache;
import io.swiftwallet.common.util.security.provider.AuthenticatedUserProvider;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class AuthenticatedUserFacade {

    @Inject
    private AuthenticatedUserProvider userProvider;
    @Inject
    private AuthenticatedUserCache authenticatedUserCache;
    @Inject
    private TokenStore tokenStore;

    public void logout(final String accessToken) {
        authenticatedUserCache.delete(accessToken);
        final OAuth2AccessToken oauth2Token = tokenStore.readAccessToken(accessToken);
        if (oauth2Token != null) {
            tokenStore.removeAccessToken(oauth2Token);
            tokenStore.removeRefreshToken(oauth2Token.getRefreshToken());
        }
    }

}