package com.yanwu.spring.cloud.common.core.jmx.health;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Collection;

/**
 * It describes the health info about current application
 */
@Data
@Accessors(chain = true)
public class ApplicationHealth {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * The name of application which should be specified by internal
     */
    private String application;

    private HealthStatus status = HealthStatus.UP;

    private Collection<ComponentHealth> healthOfComponents = new ArrayList<>();

    @JsonIgnore()
    private final StatusChangeSupport statusChangeSupport = new StatusChangeSupport(this);

    public ApplicationHealth setStatus(HealthStatus _status) {
        HealthStatus oldValue = this.status;
        this.status = _status;
        this.statusChangeSupport.firePropertyChange(StatusChangeSupport.STATUS, oldValue, status);
        return this;
    }
}
