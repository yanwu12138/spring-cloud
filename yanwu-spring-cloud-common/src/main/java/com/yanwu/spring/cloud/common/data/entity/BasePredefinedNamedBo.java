package com.yanwu.spring.cloud.common.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true, exclude = { "predefined" })
@MappedSuperclass
abstract public class BasePredefinedNamedBo extends BaseNamedBo implements Predefinable {

	private static final long serialVersionUID = -6762153795905383220L;

	@Column(name = "PREDEFINED", updatable = false)
	@NotNull
	protected Boolean predefined = false;

	@Override
	public boolean getPredefined() {
		return Boolean.TRUE.equals(predefined);
	}
}