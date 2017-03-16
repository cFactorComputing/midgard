package io.swiftwallet.midgard.common.config;

import com.gemstone.gemfire.cache.GemFireCache;
import io.swiftwallet.common.util.security.crypto.password.WalletPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientCacheFactoryBean;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.client.PoolFactoryBean;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.inject.Inject;
import java.security.SecureRandom;

@Configuration
@EnableGemfireRepositories(basePackages = {
        "io.swiftwallet.**.cache.repository.security",
        "io.swiftwallet.**.cache.repository.user"
})
@EnableTransactionManagement
public class MidgardConfiguration {
    private final PoolFactoryBean poolFactoryBean;
    private final ClientCacheFactoryBean cacheFactoryBean;

    @Inject
    public MidgardConfiguration(final PoolFactoryBean poolFactoryBean,
                                final ClientCacheFactoryBean cacheFactoryBean) {
        this.poolFactoryBean = poolFactoryBean;
        this.cacheFactoryBean = cacheFactoryBean;
    }

    @Bean
    public ClientRegionFactoryBean usersRegionFactoryBean() throws Exception {
        final ClientRegionFactoryBean cacheRegionFactoryBean = new ClientRegionFactoryBean();
        cacheRegionFactoryBean.setPool(poolFactoryBean.getPool());
        cacheRegionFactoryBean.setCache((GemFireCache) cacheFactoryBean.getObject());
        cacheRegionFactoryBean.setRegionName("users");
        return cacheRegionFactoryBean;
    }

    @Bean
    public ClientRegionFactoryBean authenticatedUsersRegionFactoryBean() throws Exception {
        final ClientRegionFactoryBean cacheRegionFactoryBean = new ClientRegionFactoryBean();
        cacheRegionFactoryBean.setPool(poolFactoryBean.getPool());
        cacheRegionFactoryBean.setCache((GemFireCache) cacheFactoryBean.getObject());
        cacheRegionFactoryBean.setRegionName("authenticated-users");
        return cacheRegionFactoryBean;
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