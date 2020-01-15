package com.yanwu.spring.cloud.gateway.filter;

import com.yanwu.spring.cloud.common.mvc.vo.base.YanwuUserVO;
import com.yanwu.spring.cloud.common.redis.Contents;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020-01-14 18:00.
 * <p>
 * description:
 */
@Configuration
public class AuthorizeGatewayFilter implements GatewayFilter {

    private static final String AUTHORIZE_TOKEN = "token";
    private static final String U_ID = "uid";

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, YanwuUserVO> loginTokenOperations;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        HttpHeaders headers = request.getHeaders();
        String token = headers.getFirst(AUTHORIZE_TOKEN);
        if (StringUtils.isBlank(token)) {
            token = request.getQueryParams().getFirst(AUTHORIZE_TOKEN);
        }
        String uid = headers.getFirst(U_ID);
        if (StringUtils.isBlank(uid)) {
            uid = request.getQueryParams().getFirst(U_ID);
        }
        ServerHttpResponse response = exchange.getResponse();
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        YanwuUserVO user = loginTokenOperations.get(Contents.LOGIN_TOKEN + uid);
        if (StringUtils.isBlank(token) || user == null || !token.equals(user.getToken())) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }
}
