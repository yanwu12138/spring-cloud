package com.yanwu.spring.cloud.common.mvc.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This class no ownerId and jsonType. Used for those objects not belong to
 * user.
 *
 * @author Administrator
 */
@Data
@EqualsAndHashCode(callSuper = false, of = {})
public abstract class BaseMonopolyNamedVo extends BaseVo {

    private static final long serialVersionUID = -7172000117978504534L;

    protected String name;

    protected String description;

}