package io.swiftwallet.midgard.security.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import io.swiftwallet.common.domain.security.AuthenticatedUser;
import io.swiftwallet.common.domain.security.GrantType;
import io.swiftwallet.common.domain.security.WalletUser;
import io.swiftwallet.common.persistence.cache.repository.security.AuthenticatedUserCache;
import io.swiftwallet.common.persistence.cache.repository.user.WalletUserCache;
import io.swiftwallet.common.util.security.provider.AuthenticatedUserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFilter extends ZuulFilter {
    @Inject
    private AuthenticatedUserCache authenticatedUserCache;
    @Inject
    private WalletUserCache walletUserCache;
    @Inject
    private AuthenticatedUserProvider userProvider;
    @Inject
    private TokenStore tokenStore;

    @Override
    public boolean shouldFilter() {
        return true;
    }

    public Object run() {
        final RequestContext context = RequestContext.getCurrentContext();
        final HttpServletRequest request = context.getRequest();
        final String accessToken = request.getParameter("access_token");

        final Object user = userProvider.user();
        if (StringUtils.isEmpty(accessToken) || user == null) {
            throw new AccessDeniedException("Access token or Authentication is empty");
        }
        final AuthenticatedUser existing = authenticatedUserCache.findOne(accessToken);
        if (existing == null) {
            addNewUserToCache(user, accessToken);
        }
        return null;
    }

    private void addNewUserToCache(final Object user, final String accessToken) {
        final OAuth2AccessToken token = tokenStore.readAccessToken(accessToken);
        if (token == null || token.isExpired()) {
            throw new AccessDeniedException("Access token is empty or expired");
        }
        final AuthenticatedUser newUser = new AuthenticatedUser();
        setUserByType(user, newUser);
        newUser.setAccessToken(token.getValue());
        newUser.setScope(token.getScope());
        newUser.setExpiration(token.getExpiration());
        final OAuth2RefreshToken refreshToken = token.getRefreshToken();
        if (refreshToken != null) {
            newUser.setRefreshToken(refreshToken.getValue());
        }
        authenticatedUserCache.save(newUser);
    }

    private void setUserByType(final Object user, final AuthenticatedUser newUser) {
        final GrantType grantType = userProvider.grantType();
        if (grantType == GrantType.USER) {
            final WalletUser walletUser = walletUserCache.findOne(((User) user).getUsername());
            newUser.setWalletUser(walletUser);
        } else if (GrantType.SYSTEM == grantType) {
            newUser.setUserId((String) user);
        }
        newUser.setGrantType(grantType);
    }

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return -1;
    }
}