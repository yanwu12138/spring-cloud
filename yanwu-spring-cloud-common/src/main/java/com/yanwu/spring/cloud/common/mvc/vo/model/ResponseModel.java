package com.yanwu.spring.cloud.common.mvc.vo.model;

import com.yanwu.spring.cloud.common.mvc.vo.ValueObject;
import lombok.Data;

@Data
public class ResponseModel {

    private ValueObject data;

    private ErrorModel error;

}