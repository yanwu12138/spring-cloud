package com.yanwu.spring.cloud.common.core.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.internal.engine.path.PathImpl;

import javax.validation.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class TraversalContext {

    @Getter
    final private Object rootBean;
    @Getter
    @Setter
    private Object parentBean;

    @Getter
    private List<String> pathStack = new ArrayList<>();

    @Getter
    Map<String, Object> properties = new HashMap<String, Object>();

    public Path getPath() {
        PathImpl path = PathImpl.createRootPath();
        for (String pathFragment : pathStack) {
            path.addPropertyNode(pathFragment);
        }
        return path;
    }

}