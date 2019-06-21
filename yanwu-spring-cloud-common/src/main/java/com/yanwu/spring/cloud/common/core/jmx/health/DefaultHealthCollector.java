package com.yanwu.spring.cloud.common.core.jmx.health;

import lombok.Getter;
import lombok.Setter;

public class DefaultHealthCollector extends AbstractHealthCollector {

    @Getter
    @Setter
    private String componentName;
    
}
