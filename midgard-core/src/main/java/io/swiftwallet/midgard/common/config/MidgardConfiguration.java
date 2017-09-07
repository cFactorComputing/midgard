package io.swiftwallet.midgard.common.config;

import in.cfcomputing.odin.core.services.gemfire.CacheRegionFactoryProvider;
import com.paytezz.commons.util.cache.CacheProvider;
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

@Configuration
@EnableGemfireRepositories(basePackages = {
        "com.paytezz.**.cache.repository.security",
        "com.paytezz.**.cache.repository.user",
        "com.paytezz.**.cache.repository.token",
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
    public ClientRegionFactoryBean userAccessCodesRegionFactoryBean() throws Exception {
        return cacheRegionFactoryProvider.provide("user-access-codes");
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