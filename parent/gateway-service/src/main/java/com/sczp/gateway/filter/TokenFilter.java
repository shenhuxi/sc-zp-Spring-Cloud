package com.sczp.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * 全局过滤器，该GlobalFilter会校验请求中是否包含了请求参数“token”，
 *
 * @author fangzhipeng
 * create 2018-11-16
 **/
public class TokenFilter implements GlobalFilter, Ordered {

    Logger logger=LoggerFactory.getLogger( TokenFilter.class );
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        MultiValueMap<String, String> params = exchange.getRequest().getQueryParams();
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token =  params.getFirst("token");
        token =  token==null? String.valueOf(headers.get("token")) :token;
        if (token == null || token.isEmpty()) {
            logger.info( "token is empty..." );
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}

