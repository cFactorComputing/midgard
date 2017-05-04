package io.swiftwallet.midgard.common.config;

import io.swiftwallet.commons.util.cache.CacheRegionFactoryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;

@Configuration
@EnableGemfireRepositories(basePackages = {
        "io.swiftwallet.**.cache.repository.security",
        "io.swiftwallet.**.cache.repository.user"
})
@EnableTransactionManagement
public class MidgardConfiguration {
    private final CacheRegionFactoryProvider cacheRegionFactoryProvider;

    @Inject
    public MidgardConfiguration(final CacheRegionFactoryProvider cacheRegionFactoryProvider) {
        this.cacheRegionFactoryProvider = cacheRegionFactoryProvider;
    }

    @Bean
    public ClientRegionFactoryBean usersRegionFactoryBean() throws Exception {
        return cacheRegionFactoryProvider.provide("users");
    }

    @Bean
    public ClientRegionFactoryBean authenticatedUsersRegionFactoryBean() throws Exception {
        return cacheRegionFactoryProvider.provide("authenticated-users");
    }

    @Bean
    public TokenStore tokenStore() {
        return new InMemoryTokenStore();
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