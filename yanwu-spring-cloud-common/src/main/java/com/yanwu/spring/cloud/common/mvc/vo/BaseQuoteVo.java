package com.yanwu.spring.cloud.common.mvc.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, of = {})
public abstract class BaseQuoteVo extends BaseVo {

	private static final long serialVersionUID = -3399698805633985723L;
	/**
	 * Because this class represents data that belongs to a particular user, the
	 * ownerId should never be null. The ownerId identifies the user.
	 */
	private Long ownerId;
}