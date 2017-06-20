package io.swiftwallet.midgard.security.oauth;

import in.cfcomputing.odin.core.services.security.password.DefaultPasswordEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Set;

@Service("clientDetailsServiceBean")
public class MidgardClientDetailsService implements ClientDetailsService {
    private final String clientId;
    private final String clientSecret;
    private final int tokenValidity;

    @Inject
    public MidgardClientDetailsService(@Value("${security.user.name}") final String clientId,
                                       @Value("${security.user.password}") final String clientSecret,
                                       @Value("${oauth2.token.validity:3600}") final int tokenValidity,
                                       final DefaultPasswordEncoder passwordEncoder) {
        this.clientId = clientId;
        this.clientSecret = passwordEncoder.encode(clientSecret);
        this.tokenValidity = tokenValidity;
    }


    @Override
    public ClientDetails loadClientByClientId(final String client) {
        final BaseClientDetails details = new BaseClientDetails();
        details.setAccessTokenValiditySeconds(tokenValidity);
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);

        final Set<String> scope = new HashSet<>();
        scope.add("read");
        scope.add("write");
        details.setScope(scope);

        final Set<String> grantTypes = new HashSet<>();
        grantTypes.add("client_credentials");
        grantTypes.add("password");
        grantTypes.add("refresh_token");
        details.setAuthorizedGrantTypes(grantTypes);

        details.setRefreshTokenValiditySeconds(tokenValidity * 2);
        return details;
    }
}
