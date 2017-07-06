package io.swiftwallet.midgard.security.oauth;


import in.cfcomputing.odin.core.services.security.oauth2.domain.OAuth2Token;
import in.cfcomputing.odin.core.services.security.oauth2.provider.GemfireTokenStore;
import io.swiftwallet.commons.domain.oauth2.token.WalletToken;
import io.swiftwallet.commons.persistence.cache.repository.token.WalletTokenCache;

public class WalletTokenStore extends GemfireTokenStore<WalletTokenCache> {

    public WalletTokenStore(final WalletTokenCache tokenCache) {
        super(tokenCache);
    }

    @Override
    protected OAuth2Token createNewToken() {
        return new WalletToken();
    }
}