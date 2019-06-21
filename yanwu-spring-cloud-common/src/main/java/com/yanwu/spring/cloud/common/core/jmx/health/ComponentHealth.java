package com.yanwu.spring.cloud.common.core.jmx.health;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.beans.PropertyChangeListener;


/**
 * Describes health info for each component
 */
@Data
@Accessors( chain = true )
@ToString
public class ComponentHealth {

    @Getter(value = AccessLevel.NONE)
    @JsonIgnore
    private final StatusChangeSupport statusChangeSupport = new StatusChangeSupport(this);

    /**
     * Component name, which is specified in each HealthCollector implementation
     */
    private String componentName;

    /**
     * The health status, up or down
     */
    private HealthStatus status = HealthStatus.UP;

    /**
     * The detailed failure cause message
     */
    private String message = "";
    
    private ComponentHealth setStatus(final HealthStatus _status) {
        HealthStatus oldValue = status;
        status = _status;
        statusChangeSupport.firePropertyChange(StatusChangeSupport.STATUS, oldValue, status);
        return this;
    }

    public ComponentHealth up() {
        message = "";
        return setStatus(HealthStatus.UP);
    }

    public ComponentHealth down(final String message) {
        this.message = message;
        return setStatus(HealthStatus.DOWN);
    }

    public ComponentHealth addPropertyChangeListener(PropertyChangeListener listener) {
        statusChangeSupport.addPropertyChangeListener(listener);
        return this;
    }

}
