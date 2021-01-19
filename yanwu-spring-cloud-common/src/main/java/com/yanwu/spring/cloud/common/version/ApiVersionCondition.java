package com.yanwu.spring.cloud.common.version;

import com.yanwu.spring.cloud.common.core.common.Contents;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.mvc.condition.RequestCondition;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Baofeng Xu
 * @date 2021/1/19 14:32.
 * <p>
 * description:
 */
public class ApiVersionCondition implements RequestCondition<ApiVersionCondition> {
    private static final String SPLIT = "\\.";

    @Getter
    @Setter
    private String apiVersion;

    public ApiVersionCondition(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    @Override
    public ApiVersionCondition combine(ApiVersionCondition other) {
        return new ApiVersionCondition(other.getApiVersion());
    }

    @Override
    public ApiVersionCondition getMatchingCondition(HttpServletRequest request) {
        String version = request.getHeader(Contents.VERSION);
        if (StringUtils.isBlank(version)) {
            return null;
        }
        return StringUtils.compareIgnoreCase(version, this.apiVersion) >= 0 ? this : null;
    }

    @Override
    public int compareTo(ApiVersionCondition other, HttpServletRequest request) {
        return StringUtils.compareIgnoreCase(other.getApiVersion(), this.getApiVersion());
    }

}
