package com.yanwu.spring.cloud.gateway.handler;

import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.pojo.AccessToken;
import com.yanwu.spring.cloud.common.utils.TokenUtil;
import com.yanwu.spring.cloud.gateway.bo.YanwuUserVO;
import com.yanwu.spring.cloud.gateway.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.yanwu.spring.cloud.common.core.common.Contents.*;


/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/1 16:20.
 * <p>
 * description:
 */
@Slf4j
@Component
public class GatewayHandlerFilter implements GlobalFilter, Ordered {

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    private ValueOperations<String, YanwuUserVO> loginTokenOperations;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // ----- 处理请求头 && 权限校验
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();
        if (AuthConfig.getPassOperations().contains(path)) {
            // ----- 白名单
            return chain.filter(filter(exchange));
        } else {
            // ----- 校验token, 刷新token
            String token = exchange.getRequest().getHeaders().getFirst(TOKEN);
            AccessToken accessToken = TokenUtil.verifyToken(token);
            YanwuUserVO user = loginTokenOperations.get(LOGIN_TOKEN + accessToken.getId());
            Assert.notNull(user, "no token!");
            loginTokenOperations.set(Contents.LOGIN_TOKEN + user.getId(), user, Contents.TOKEN_TIME_OUT, TimeUnit.SECONDS);
            checkPath(path, user.getId());
            return chain.filter(filter(exchange));
        }
    }

    private ServerWebExchange filter(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String txId = request.getQueryParams().getFirst(TX_ID);
        if (StringUtils.isBlank(txId)) {
            txId = UUID.randomUUID().toString();
        }
        log.info("Gateway   : [txId]: {}, {}: {}", txId, LOG_METHOD, request.getPath());
        // ----- 处理请求头
        request = exchange.getRequest().mutate().header(TX_ID, txId).build();
        // ----- 处理响应头
        response.getHeaders().add(TX_ID, txId);
        return exchange.mutate().request(request).build().mutate().response(response).build();
    }

    @Override
    public int getOrder() {
        return -2;
    }


    /**
     * 检查接口是否合法
     *
     * @param path 路径
     */
    private Boolean checkPath(String path, Long userId) {
        return true;
    }
}
