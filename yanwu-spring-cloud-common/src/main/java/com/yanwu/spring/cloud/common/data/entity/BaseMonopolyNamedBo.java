package com.yanwu.spring.cloud.common.data.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
abstract public class BaseMonopolyNamedBo extends BaseDo<Long> {

    private static final long serialVersionUID = -8709322445035506545L;
    public static final String MONOPOLY_OBJECT_ERROR_CODE_PREFIX = "monoploly.object.do.validator.";
    public static final String NAME_REGEXP = "[A-Za-z0-9!#\\$%&'\\(\\)\\*\\+,\\-\\./:;<=>@\\[\\]\\\\\\^_`\\{\\|\\}~\\s]*";

    @Size(min = 1, max = DEFAULT_STRING_LENGTH)
    @Column(name = "NAME", length = DEFAULT_STRING_LENGTH)
    protected String name;

    @Size(max = DEFAULT_DESCRIPTION_LENGTH, message = "{" + MONOPOLY_OBJECT_ERROR_CODE_PREFIX
            + "description.max.length}")
    @Column(name = "DESCRIPTION", length = DEFAULT_DESCRIPTION_LENGTH)
    protected String description;

    @Override
    public String getLogging() {
        return name;
    }
}
