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
        DataBufferFactory bufferFactory = response.bufferFactory();
        ServerHttpResponseDecorator responseDecorator = new ServerHttpResponseDecorator(response) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        // probably should reuse buffers
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        // 释放掉内存
                        DataBufferUtils.release(dataBuffer);
                        String rs = new String(content, Charset.forName("UTF-8"));
                        Map<String, Serializable> map = new HashMap<>(4);
                        map.put("data", rs);
                        map.put("code", 200);
                        map.put("message", "请求成功");
                        byte[] newRs = JSON.toJSONString(map).getBytes(Charset.forName("UTF-8"));
                        // --- 如果不重新设置长度则收不到消息。
                        response.getHeaders().add(TX_ID, txId);
                        response.getHeaders().setContentLength(newRs.length);
                        return bufferFactory.wrap(newRs);
                    }));
                }
                // if body is not a flux. never got there.
                return super.writeWith(body);
            }
        };
        return exchange.mutate().response(responseDecorator).build();
    }

}
