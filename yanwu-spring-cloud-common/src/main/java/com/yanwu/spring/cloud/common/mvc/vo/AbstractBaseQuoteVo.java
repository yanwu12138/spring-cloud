package com.yanwu.spring.cloud.common.mvc.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false, of = {})
public abstract class AbstractBaseQuoteVo extends BaseQuoteVo {

    private static final long serialVersionUID = -562110294729486345L;
    /**
     * This is needed to get around Jackson JSON decoding issue. Due to failure
     * to detect generic types, in many cases the type information is not
     * attached in JSON payload, causing integration to fail. An abstract value
     * object must be extended from this class to get attribute jsonType
     * defined.
     */
    private String jsonType;

}