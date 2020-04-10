package com.yanwu.spring.cloud.common.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Administrator
 */
@Data
@Accessors(chain = true)
@JsonInclude(Include.NON_NULL)
public class RestApiError implements Serializable {
    private static final long serialVersionUID = 6773758863518219601L;

    public final static String CODE_OK = "0";

    public final static String MESSAGE_SUCCEED = "";

    /**
     * HTTP Status
     */
    private int status;

    /**
     * Business Code
     */
    private String code;

    /**
     * The localized message with substituted parameters.
     */
    private String message;

    /**
     * map of parameters that the client can use to generate it's own error
     * message if needed
     */
    private Map<String, Object> errorParams;

}