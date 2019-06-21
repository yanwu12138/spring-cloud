package com.yanwu.spring.cloud.common.core.jmx.health;

public interface FaultPointerSupport {
	
	void raise(String pointerId, Object message);

	void clear(String pointerId);

}
