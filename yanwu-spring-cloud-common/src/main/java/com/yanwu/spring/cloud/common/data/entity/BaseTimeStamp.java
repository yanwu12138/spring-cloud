package com.yanwu.spring.cloud.common.data.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
public abstract class BaseTimeStamp implements Serializable {

    private static final long serialVersionUID = -4739004529406208601L;

    @Getter
    @Column(name = "CREATED_AT")
    private Timestamp createdAt;

    @Getter
    @Setter
    @Column(name = "UPDATED_AT")
    @Version
    private Timestamp updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = new Timestamp(System.currentTimeMillis());
    }

    @PreUpdate
    protected void onUpdate() {
        if (updatedAt == null) {
            updatedAt = new Timestamp(System.currentTimeMillis());
        }
    }
}