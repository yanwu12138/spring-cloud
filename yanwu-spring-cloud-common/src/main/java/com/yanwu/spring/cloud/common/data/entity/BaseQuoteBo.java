package com.yanwu.spring.cloud.common.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = false, of = {})
@MappedSuperclass
public abstract class BaseQuoteBo extends BaseDo<Long> implements ShardedResource {

	final static private long serialVersionUID = -455496693445828077L;

	@Column(name = "OWNER_ID", updatable = false)
	@NotNull
	protected Long ownerId;

}