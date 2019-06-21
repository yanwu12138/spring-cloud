package com.yanwu.spring.cloud.common.core.jmx.health;

import java.beans.PropertyChangeSupport;

public class StatusChangeSupport extends PropertyChangeSupport {

    private static final long serialVersionUID = 6351629696890403204L;

    public StatusChangeSupport(Object sourceBean) {
        super(sourceBean);
    }

    public static final String STATUS = "status";

}
