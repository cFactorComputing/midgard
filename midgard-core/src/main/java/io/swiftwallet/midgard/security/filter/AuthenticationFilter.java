package io.swiftwallet.midgard.security.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.paytezz.commons.domain.security.AuthenticatedUser;
import com.paytezz.commons.domain.security.WalletUser;
import com.paytezz.commons.domain.security.WalletUserAccessCode;
import com.paytezz.commons.persistence.cache.repository.security.AuthenticatedUserCache;
import com.paytezz.commons.persistence.cache.repository.user.UserAccessCodeCache;
import in.cfcomputing.odin.core.services.security.domain.GrantType;
import in.cfcomputing.odin.core.services.security.oauth2.domain.AuthenticatedUserDetails;
import in.cfcomputing.odin.core.services.security.provider.AuthenticatedUserProvider;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Component
public class AuthenticationFilter extends ZuulFilter {
    private final AuthenticatedUserCache authenticatedUserCache;
    private final AuthenticatedUserProvider userProvider;
    private final UserAccessCodeCache quickAccessCodeCache;
    private final TokenStore tokenStore;

    @Inject
    public AuthenticationFilter(final AuthenticatedUserCache authenticatedUserCache,
                                final UserAccessCodeCache quickAccessCodeCache,
                                final AuthenticatedUserProvider userProvider,
                                final TokenStore tokenStore) {
        this.authenticatedUserCache = authenticatedUserCache;
        this.userProvider = userProvider;
        this.quickAccessCodeCache = quickAccessCodeCache;
        this.tokenStore = tokenStore;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
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
        } else {
            updateUserInCache(user, existing);
        }
        return null;
    }

    private void updateUserInCache(final Object user, final AuthenticatedUser existing) {
        setUserByType(user, existing);
        authenticatedUserCache.save(existing);
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

    private void setQuickAccessCode(final String machineId, final AuthenticatedUser user) {
        if (StringUtils.isNotEmpty(machineId)) {
            final WalletUserAccessCode accessCode = quickAccessCodeCache.findOne(machineId);
            user.setQuickAccessCode(accessCode);
        }
    }

    private void setUserByType(final Object user, final AuthenticatedUser newUser) {
        final GrantType grantType = userProvider.grantType();
        if (grantType == GrantType.USER) {
            final AuthenticatedUserDetails<WalletUser> userDetails = (AuthenticatedUserDetails) user;
            final WalletUser walletUser = userDetails.getAuthenticatedUser();
            newUser.setWalletUser(walletUser);
            setQuickAccessCode(walletUser.getDeviceId(), newUser);
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