/**
 *
 */
package com.yanwu.spring.cloud.common.mvc.controller;

/**
 * Base class for the REST MVC controller.
 * @author Administrator
 */
public class BaseController {
//	@Autowired
//	protected View jsonView; // Only JSON view for now.

    protected static final String DATA_FIELD = "data";
    protected static final String ERROR_FIELD = "error";

    protected static boolean isEmpty(String s_p) {
        return (null == s_p) || s_p.trim().length() == 0;
    }

    /**
     *
     */
    public BaseController() {
        // TODO Auto-generated constructor stub
    }

}
