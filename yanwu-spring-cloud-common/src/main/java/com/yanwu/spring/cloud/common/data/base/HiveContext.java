package com.yanwu.spring.cloud.common.data.base;

import com.yanwu.spring.cloud.common.data.sharding.HiveRoutingKey;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.context.ApplicationContext;

import java.util.Set;

/**
 * This is essentially the application context for cloud 3.0
 */
@Data
@Accessors(chain = true)
public class HiveContext {

    private ApplicationContext applicationContext;
    private Long customerId;
    private String externalCustomerId;
    private Long userId;
    private String userName;
    private String accessToken;
    private String vhmId;
    private String hostIp;
    private String appId;
    private String sn;
    private String reqUrl;
    private HiveRoutingKey routingKey;
    private Boolean rbacAllowAll;
    private Set<Long> rbacLocationSet;

}
