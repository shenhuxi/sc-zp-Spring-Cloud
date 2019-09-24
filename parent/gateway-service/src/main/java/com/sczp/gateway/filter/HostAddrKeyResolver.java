package com.sczp.gateway.filter;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 限流器
 */
public class HostAddrKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        Mono<String> just = Mono.just(exchange.getRequest().getRemoteAddress().getAddress().getHostAddress());
        return just;
    }
}
