package com.yanwu.spring.cloud.common.data.entity;

public interface ShardedResource {

	final long GLOBAL_OWNER_ID = -1L;

	Long getOwnerId();
}
