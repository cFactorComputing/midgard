package io.swiftwallet.midgard.common.config;

import in.cfcomputing.odin.core.services.gemfire.CacheRegionFactoryProvider;
import io.swiftwallet.commons.persistence.cache.repository.token.WalletTokenCache;
import io.swiftwallet.commons.util.cache.CacheProvider;
import io.swiftwallet.midgard.security.oauth.WalletTokenStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableGemfireRepositories(basePackages = {
        "io.swiftwallet.**.cache.repository.security",
        "io.swiftwallet.**.cache.repository.user",
        "io.swiftwallet.**.cache.repository.token",
})
@EnableTransactionManagement
public class MidgardConfiguration {
    private final CacheRegionFactoryProvider cacheRegionFactoryProvider;
    private final CacheProvider cacheProvider;

    public MidgardConfiguration(final CacheRegionFactoryProvider cacheRegionFactoryProvider,
                                final CacheProvider cacheProvider) {
        this.cacheRegionFactoryProvider = cacheRegionFactoryProvider;
        this.cacheProvider = cacheProvider;
    }

    @Bean
    public ClientRegionFactoryBean usersRegionFactoryBean() throws Exception {
        return cacheRegionFactoryProvider.provide("users");
    }

    @Bean
    public ClientRegionFactoryBean tokenRegionFactoryBean() throws Exception {
        return cacheRegionFactoryProvider.provide("token-store");
    }

    @Bean
    public ClientRegionFactoryBean authenticatedUsersRegionFactoryBean() throws Exception {
        return cacheRegionFactoryProvider.provide("authenticated-users");
    }

    @Bean
    public TokenStore tokenStore() {
        return new WalletTokenStore(cacheProvider.provide(WalletTokenCache.class));
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return new DoNothingTransactionManager();
    }

    /**
     * Empty transaction manager bean. The Spring OAuth framework expects a transaction manager
     * to be present in the context.
     */
    static class DoNothingTransactionManager implements PlatformTransactionManager {
        @Override
        public TransactionStatus getTransaction(TransactionDefinition transactionDefinition) throws TransactionException {
            return null;
        }

        @Override
        public void commit(TransactionStatus transactionStatus) throws TransactionException {
        }

        @Override
        public void rollback(TransactionStatus transactionStatus) throws TransactionException {
        }
    }
}