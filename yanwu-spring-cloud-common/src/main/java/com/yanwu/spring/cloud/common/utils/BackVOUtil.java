package com.yanwu.spring.cloud.common.utils;

import com.yanwu.spring.cloud.common.core.exception.ExceptionDefinition;
import com.yanwu.spring.cloud.common.mvc.res.BackVO;

/**
 * @author XuBaofeng.
 * @date 2018-09-29 22:11.
 * <p>
 * description: 这个类仅使用在提供接口给web使用的controller中
 */
public class BackVOUtil {

    /**
     * 当controller捕获到异常时, 如果是
     *
     * @param e
     * @return
     */
    public static <T> BackVO<T> operateError(Exception e) {
        String message = e.getMessage();
        Integer code = -800000;
        if (!ExceptionDefinition.isExist(message)) {
            code = ExceptionDefinition.SYSTEM_ERROR.code;
            message = ExceptionDefinition.SYSTEM_ERROR.key;
        }
        return operateError(code, message);
    }

    /**
     * 当web端传递的参数不符合该接口的参数定义, 则直接返回该BackVO
     *
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    public static <T> BackVO<T> operateError(Integer code, String message) {
        return assembleErrorBackVO(code, message);
    }

    /**
     * 封装更新操作没有返回值
     *
     * @return
     */
    public static <T> BackVO<T> operateAccess() {
        return operateAccess(null);
    }

    public static <T> BackVO<T> operateAccess(T data) {
        return operateAccess(data, null);
    }

    public static <T> BackVO<T> operateAccess(T data, String message) {
        return assembleAccessBackVO(data, message);
    }

    /**
     * 组建请求错误返回的vo
     *
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    private static <T> BackVO<T> assembleErrorBackVO(Integer code, String message) {
        return assembleBackVO(null, code, message, Boolean.FALSE);
    }

    /**
     * 组建请求正常返回组建的vo
     *
     * @param data
     * @param message
     * @param <T>
     * @return
     */
    private static <T> BackVO<T> assembleAccessBackVO(T data, String message) {
        return assembleBackVO(data, null, message, Boolean.TRUE);
    }

    /**
     * 组建vo
     *
     * @param data
     * @param code
     * @param message
     * @param <T>
     * @return
     */
    private static <T> BackVO<T> assembleBackVO(T data, Integer code, String message, Boolean boo) {
        BackVO<T> backVO = new BackVO<>();
        backVO.setData(data);
        backVO.setCode(code);
        backVO.setMessage(message);
        backVO.setStatus(boo);
        return backVO;
    }

}
