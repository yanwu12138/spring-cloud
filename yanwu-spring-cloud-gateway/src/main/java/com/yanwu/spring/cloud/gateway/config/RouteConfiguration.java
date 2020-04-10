package com.yanwu.spring.cloud.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/7 19:37.
 * <p>
 * description:
 */
@Configuration
public class RouteConfiguration {

    /**
     * 根据IP地址去进行请求过滤
     *
     * @return 返回对应的令牌
     */
    @Bean
    public KeyResolver routeKeyResolver() {
        // ===== IP地址限流
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getLocalAddress()).getAddress().toString());
        // ===== 用户进行限流
//        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst(Constant.CURRENT_USER)));
        // ===== TOKEN进行限流
//        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst(Constant.TOKEN)));
    }

}
