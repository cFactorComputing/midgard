package io.swiftwallet.midgard.core.cb.config;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by gibugeorge on 10/02/2017.
 */
@Configuration
public class CircuiteBreakerConfiguration {

    @Bean
    public HystrixCommandAspect hiHystrixCommandAspect() {
        return new HystrixCommandAspect();
    }

}
