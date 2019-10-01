package com.sczp.gateway;

import brave.sampler.Sampler;
import com.sczp.gateway.filter.RequestTimeFilter;
import com.sczp.gateway.filter.TokenFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class WebConfig {
    //限流器注入ioc
    //@Bean
   /* public HostAddrKeyResolver hostAddrKeyResolver() {
        return new HostAddrKeyResolver();
    }*/

    @Bean
    public KeyResolver hostAddrKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName());
    }
    //全局过滤判断token器注入ioc
    @Bean
    public TokenFilter tokenFilter(){
        return new TokenFilter();
    }
    //@Bean
    public RouteLocator customerRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/customer/**")
                        .filters(f -> f.filter(new RequestTimeFilter())
                                .addResponseHeader("X-Response-Default-Foo", "Default-Bar"))
                        .uri("http://httpbin.org:80/get")
                        .order(0)
                        .id("customer_filter_router")
                )
                .build();
    }

    //注入Spring cloud sleuth需要的bean
    @Bean
    public Sampler defaultSampler() {
        return Sampler.ALWAYS_SAMPLE;
    }
}
