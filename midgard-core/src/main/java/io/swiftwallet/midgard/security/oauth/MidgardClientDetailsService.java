package io.swiftwallet.midgard.security.oauth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service("clientDetailsServiceBean")
public class MidgardClientDetailsService implements ClientDetailsService {

    @Value("${security.user.name}")
    private String clientId;

    @Value("${security.user.password}")
    private String clientSecret;

    @Value("${oauth2.token.validity:3600}")
    private int tokenValidity;


    @Override
    public ClientDetails loadClientByClientId(final String client) throws ClientRegistrationException {
        final BaseClientDetails details = new BaseClientDetails();
        details.setAccessTokenValiditySeconds(tokenValidity);
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);

        final Set<String> scope = new HashSet<>();
        scope.add("read");
        scope.add("write");
        details.setScope(scope);

        return details;
    }
}
