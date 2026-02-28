package com.wallet.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

@Configuration
public class RateLimiterConfig {

    @Bean
    public KeyResolver remoteAddressKeyResolver() {
        // Resolve Rate Limiting Key by the Client's Origin IP Address
        return exchange -> {
            InetSocketAddress remoteAddress = exchange.getRequest().getRemoteAddress();
            String key = remoteAddress != null ? remoteAddress.getAddress().getHostAddress() : "anonymous";
            return Mono.just(key);
        };
    }
}
