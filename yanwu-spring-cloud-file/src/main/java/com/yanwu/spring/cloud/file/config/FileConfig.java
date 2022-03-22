package com.yanwu.spring.cloud.file.config;

import lombok.Data;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Baofeng Xu
 * @date 2021/1/21 14:58.
 * <p>
 * description:
 */
@Data
@Component
public class FileConfig {

    /*** ----------- 二维码相关配置 ----------- ***/
    @Value("${qrcode.check.url}")
    private String checkQrCodeUrl;
    @Value("${qrcode.file.path}")
    private String codeFilePath;


    /*** ----------- lucene相关配置 ----------- ***/
    @Value("${lucene.index}")
    private String luceneIndex;

    /*** ----------- zookeeper相关配置 ----------- ***/
    @Value("${zookeeper.path}")
    private String zookeeperPath;
    @Value("${zookeeper.timeout}")
    private Integer zookeeperTimeout;
    @Value("${zookeeper.backoff.baseSleepTime}")
    private Integer zookeeperBaseSleepTime;
    @Value("${zookeeper.backoff.maxRetries}")
    private Integer zookeeperMaxRetries;

    /*** ----------- elasticsearch相关配置 ----------- ***/
    @Value("${elasticsearch.host:127.0.0.1}")
    private String elasticsearchHost;
    private HttpHost elasticsearchHttpHost = null;
    @Value("${elasticsearch.port:9200}")
    private Integer elasticsearchPort;
    @Value("${elasticsearch.schema:http}")
    private String elasticsearchSchema;
    @Value("${elasticsearch.timeout.socket:30000}")
    private Integer elasticsearchSocketTimeOut = 30_000;
    @Value("${elasticsearch.timeout.connect:1000}")
    private Integer elasticsearchConnectTimeOut = 1_000;
    @Value("${elasticsearch.timeout.connectionRequest:500}")
    private Integer elasticsearchConnectionRequestTimeOut = 500;
    @Value("${elasticsearch.maxConnectNum:100}")
    private Integer elasticsearchMaxConnectNum = 100;
    @Value("${elasticsearch.maxConnectPerRoute:100}")
    private Integer elasticsearchMaxConnectPerRoute = 100;

    @PostConstruct
    public void init() {
        // ===== elasticsearch 客户端
        elasticsearchHttpHost = new HttpHost(elasticsearchHost, elasticsearchPort, elasticsearchSchema);
    }

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(elasticsearchHttpHost);
        // ----- 异步httpclient连接延时配置
        builder.setRequestConfigCallback((requestConfigBuilder) -> {
            requestConfigBuilder.setConnectTimeout(elasticsearchConnectTimeOut);
            requestConfigBuilder.setSocketTimeout(elasticsearchSocketTimeOut);
            requestConfigBuilder.setConnectionRequestTimeout(elasticsearchConnectionRequestTimeOut);
            return requestConfigBuilder;
        });
        // ----- 异步httpClient连接数配置
        builder.setHttpClientConfigCallback((clientBuilder) -> {
            clientBuilder.setMaxConnTotal(elasticsearchMaxConnectNum);
            clientBuilder.setMaxConnPerRoute(elasticsearchMaxConnectPerRoute);
            return clientBuilder;
        });
        return new RestHighLevelClient(builder);
    }

}
