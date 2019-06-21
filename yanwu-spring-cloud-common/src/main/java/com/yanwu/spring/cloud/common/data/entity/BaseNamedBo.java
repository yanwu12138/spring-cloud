package com.yanwu.spring.cloud.common.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
abstract public class BaseNamedBo extends BaseQuoteBo {

    private static final long serialVersionUID = -2689794473979217258L;
    public static final String SHARED_OBJECT_ERROR_CODE_PREFIX = "shared.object.do.validator.";
    public static final String NAME_REGEXP = "[A-Za-z0-9!#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>@\\[\\]\\\\\\^_`\\{\\|\\}~\\s]*";

    @NotNull
    @Pattern(regexp = NAME_REGEXP, message = "{" + SHARED_OBJECT_ERROR_CODE_PREFIX + "name.not.valid}")
    @Size(min = 1, max = DEFAULT_STRING_LENGTH)
    @Column(name = "NAME", length = DEFAULT_STRING_LENGTH)
    protected String name;

    @Size(max = DEFAULT_DESCRIPTION_LENGTH, message = "{" + SHARED_OBJECT_ERROR_CODE_PREFIX + "description.max.length}")
    @Column(name = "DESCRIPTION", length = DEFAULT_DESCRIPTION_LENGTH)
    protected String description;
}