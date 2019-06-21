package com.yanwu.spring.cloud.common.mvc.vo.model;

import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import lombok.Data;

@Data
public class ErrorModel implements ValueObject {

    private static final long serialVersionUID = 3941455114800111787L;

    private int error_code;

    private String error_msg;

}