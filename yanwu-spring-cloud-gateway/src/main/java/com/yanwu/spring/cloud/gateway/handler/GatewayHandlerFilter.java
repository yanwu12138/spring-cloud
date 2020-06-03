package com.yanwu.spring.cloud.gateway.handler;

import com.yanwu.spring.cloud.common.core.common.Contents;
import com.yanwu.spring.cloud.common.pojo.AccessToken;
import com.yanwu.spring.cloud.common.utils.JsonUtil;
import com.yanwu.spring.cloud.common.utils.TokenUtil;
import com.yanwu.spring.cloud.gateway.bo.YanwuUserVO;
import com.yanwu.spring.cloud.gateway.config.AuthConfig;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
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
        String txId = UUID.randomUUID().toString();
        // ----- 处理请求头
        request = exchange.getRequest().mutate().header(TX_ID, txId).build();
        exchange = exchange.mutate().request(request).build();
        // ----- 处理响应头
        response.getHeaders().add(TX_ID, txId);
        exchange = exchange.mutate().response(response).build();
        // ----- 处理日志
        ServerHttpResponseDecorator decorator = logHandler(exchange, request, response, txId);
        return exchange;
    }

    private ServerHttpResponseDecorator logHandler(ServerWebExchange exchange, ServerHttpRequest request, ServerHttpResponse response, String txId) {
        // ===== 输出日志
        String tmoParam = null;
        switch (Objects.requireNonNull(request.getMethod())) {
            case GET:
            case DELETE:
                tmoParam = JsonUtil.toJsonString(request.getQueryParams());
            case PUT:
            case POST:
                MediaType contentType = request.getHeaders().getContentType();
                if (!MediaType.APPLICATION_JSON.equals(contentType)) {
                    break;
                }
                DataBuffer dataBuffer = exchange.getAttribute(ServerWebExchangeUtils.CACHED_REQUEST_BODY_ATTR);
                dataBuffer = dataBuffer.retainedSlice(0, dataBuffer.readableByteCount());
                byte[] buffer = new byte[dataBuffer.readableByteCount()];
                dataBuffer.read(buffer);
                DataBufferUtils.release(dataBuffer);
                tmoParam = new String(buffer, StandardCharsets.UTF_8);
                break;
            default:
                break;
        }
        String path = request.getPath().toString();
        log.info("Gateway   : [txId]: {}, {}: {}, {}: {}, {}: {}", txId, METHOD_TYPE, request.getMethodValue(), METHOD, path, PARAM, tmoParam);
        // ===== 重新包装请求头域响应头
        String param = tmoParam;
        DataBufferFactory bufferFactory = response.bufferFactory();
        return new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        String bodyContent = new String(content, StandardCharsets.UTF_8);
                        log.info("Gateway   : [txId]: {}, {}: {}", txId, RESULT, bodyContent);
                        // 异步执行
//                        logExecutor.execute(() -> createLog(path, param, bodyContent, txId, 1L))
                        return bufferFactory.wrap(content);
                    }));
                }
                return super.writeWith(body);
            }
        };
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
