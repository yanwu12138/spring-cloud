package com.yanwu.spring.cloud.common.mvc.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class BaseNamedVoAbstract extends AbstractFeatureVo {

	private static final long serialVersionUID = -4744720246761936965L;

	protected String name;

	protected String description;

}