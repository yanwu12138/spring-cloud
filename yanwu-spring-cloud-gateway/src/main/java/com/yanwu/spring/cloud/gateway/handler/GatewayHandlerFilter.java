package com.yanwu.spring.cloud.gateway.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.yanwu.spring.cloud.gateway.handler.Constant.*;


/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2020/4/1 16:20.
 * <p>
 * description:
 */
@Slf4j
@Component
public class GatewayHandlerFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String txId = exchange.getRequest().getQueryParams().getFirst(TX_ID);
        if (StringUtils.isBlank(txId)) {
            txId = UUID.randomUUID().toString();
        }
        exchange = requestHandler(exchange, txId);
        exchange = responseHandler(exchange, txId);
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -2;
    }

    /**
     * 处理请求头
     *
     * @param exchange .
     * @param txId     .
     * @return .
     */
    private ServerWebExchange requestHandler(ServerWebExchange exchange, String txId) {
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        log.info("Gateway   : [txId]: {}, {}: {}, {}: {}", txId, LOG_METHOD, request.getPath(), LOG_PARAM, queryParams);
        request = exchange.getRequest().mutate().header(TX_ID, txId).build();
        return exchange.mutate().request(request).build();
    }

    /**
     * 处理响应
     *
     * @param exchange .
     * @param txId     .
     * @return .
     */
    private ServerWebExchange responseHandler(ServerWebExchange exchange, String txId) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().add(TX_ID, txId);
        return exchange.mutate().response(response).build();
    }

}
