package com.yanwu.spring.cloud.common.core.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class AccessToken implements Serializable {

	private String expire;

	private Long userId;

}
