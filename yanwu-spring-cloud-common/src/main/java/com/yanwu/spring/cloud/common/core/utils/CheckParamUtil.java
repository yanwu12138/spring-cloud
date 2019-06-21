/**
 * zjdyiot.com Inc.
 * Copyright (c) 2013-2017 All Rights Reserved.
 */
package com.yanwu.spring.cloud.common.core.utils;

import com.yanwu.spring.cloud.common.core.exception.BusinessException;
import com.yanwu.spring.cloud.common.core.exception.ExceptionDefinition;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public final class CheckParamUtil {

    /**
     * 验证字符串不为空
     *
     * @param param
     * @param msgValues
     */
    public static void checkStringNotBlank(String param, Object... msgValues) {
        if (StringUtils.isBlank(param) || StringUtils.equalsIgnoreCase("null", param)) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkStringNotBlank", msgValues);
        }
    }

    /**
     * 验证字符串为null,不为null抛异常
     *
     * @param param
     * @param msgValues
     */
    public static void checkStringBlank(String param, Object... msgValues) {
        if (StringUtils.isNotBlank(param)) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkStringBlank", msgValues);
        }
    }

    /**
     * 验证输入参数必须要大于0
     *
     * @param param
     * @param msgValues
     */
    public static void checkLongNotThanZero(Long param, Object... msgValues) {
        if (param <= 0) {
            throw new BusinessException(ExceptionDefinition.THE_VALUE_IS_LESS_THAN_ZERO.code, ExceptionDefinition.THE_VALUE_IS_LESS_THAN_ZERO.key, "checkLongNotThanZero", msgValues);
        }
    }

    /**
     * 验证输入参数必须要大于0
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkLongNotThanZero(Long param, BusinessException e, Object... msgValues) {
        if (param <= 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证输入参数必须要大于0
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkLongNotLessZero(Long param, BusinessException e, Object... msgValues) {
        if (param < 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证输入参数必须要大于0
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkDoubleNotThanZero(Double param, BusinessException e, Object... msgValues) {
        if (param <= 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证输入参数必须要大于0
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkDoubleNotLessZero(Double param, BusinessException e, Object... msgValues) {
        if (param < 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证字符串不为空且必须为数字
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkStringIsNumber(String param, BusinessException e, Object... msgValues) {
        try {
            Double.parseDouble(param);
        } catch (Exception exception) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证对象不为null，为null抛异常
     *
     * @param param
     * @param msgValues
     */
    public static void checkObjectNotNull(Object param, Object... msgValues) {
        if (objectIsEmpty(param)) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkObjectNotNull", msgValues);
        }
    }

    /**
     * 验证对象不为null，为null抛异常
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkObjectNotNull(Object param, BusinessException e, Object... msgValues) {
        if (objectIsEmpty(param)) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证对象不为null，为null抛异常，不为null返回当前对象
     *
     * @param param
     * @param e
     * @param msgValues
     * @return
     */
    public static <T> T checkObjectNotNullAndRtn(T param, BusinessException e, Object... msgValues) {
        if (objectIsEmpty(param)) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
        return param;
    }

    /**
     * 验证字符串相等，相等就通过，不相等就抛异常
     *
     * @param param1
     * @param param2
     * @param e
     * @param msgValues
     */
    public static void checkStringEquals(String param1, String param2, ExceptionDefinition.CodeAndKey e, Object... msgValues) {
        if (!StringUtils.equals(param1, param2)) {
            throw new BusinessException(e.code, e.key, "checkStringEquals", msgValues);
        }
    }

    /**
     * 验证字符串不相等，相等就抛异常，不相等就通过
     *
     * @param param1
     * @param param2
     * @param e
     * @param msgValues
     */
    public static void CheckStringNotEquals(String param1, String param2, BusinessException e, Object... msgValues) {
        if (StringUtils.equals(param1, param2)) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * bigDecimal类型验证,数据<=0抛异常
     *
     * @param bigDecimal
     * @param e
     * @param msgValues
     */
    public static void checkBigDecimalNotNullAndNotThanZero(BigDecimal bigDecimal, BusinessException e, Object... msgValues) {
        if (bigDecimal == null) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
        checkLongNotThanZero(Long.valueOf(bigDecimal.compareTo(BigDecimal.ZERO)), e, msgValues);
    }

    /**
     * bigDecimal类型验证,数据<0抛异常
     *
     * @param bigDecimal
     * @param e
     * @param msgValues
     */
    public static void checkBigDecimalNotNullAndNotLessZero(BigDecimal bigDecimal, BusinessException e, Object... msgValues) {
        if (bigDecimal == null) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
        checkLongNotLessZero(Long.valueOf(bigDecimal.compareTo(BigDecimal.ZERO)), e, msgValues);
    }

    /**
     * 验证对象为null，不为null抛异常
     *
     * @param param
     * @param e
     * @param msgValues
     */
    public static void checkObjectIsNull(Object param, ExceptionDefinition.CodeAndKey e, Object... msgValues) {
        if (!objectIsEmpty(param)) {
            throw new BusinessException(e.code, e.key, "checkObjectIsNull", msgValues);
        }
    }

    /**
     * 验证集合对象非空且长度大于0 为空抛异常
     *
     * @param list
     * @param msgValues
     */
    public static void checkListNotNullAndSizeGreaterZero(List<?> list, Object... msgValues) {
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkListNotNullAndSizeGreaterZero", msgValues);
        }
    }

    /**
     * 验证集合对象非空且长度大于0 为空抛异常
     *
     * @param list
     * @param e
     * @param msgValues
     */
    public static void checkListNotNullAndSizeGreaterZero(List<?> list, ExceptionDefinition.CodeAndKey e, Object... msgValues) {
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(e.code, e.key, "checkListNotNullAndSizeGreaterZero", msgValues);
        }
    }

    /**
     * 验证数组对象非空且长度大于0 为空抛异常
     *
     * @param objs
     * @param e
     * @param msgValues
     */
    public static void checkArrayNotNullAndSizeGreaterZero(Object[] objs, BusinessException e, Object... msgValues) {
        if (objs == null) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
        if (objs.length == 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证数组对象非空且长度大于0 为空抛异常
     *
     * @param objs
     * @param msgValues
     */
    public static void checkArrayNotNullAndSizeGreaterZero(Object[] objs, Object... msgValues) {
        if (objs == null) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkListNotNullAndSizeGreaterZero", msgValues);
        }
        if (objs.length == 0) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkListNotNullAndSizeGreaterZero", msgValues);
        }
    }

    /**
     * 若num2小于num1，抛异常
     *
     * @param num1
     * @param num2
     * @param msgValues
     */
    public static void checkIntCompare(Integer num1, Integer num2, Object... msgValues) {
        if (num1 == null || num2 == null || num1 <= 0 || num2 <= 0) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkIntCompare", msgValues);
        }
        if (num2 < num1) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkIntCompare", msgValues);
        }
    }

    /**
     * 若num2小于num1，抛异常
     *
     * @param num1
     * @param num2
     * @param msgValues
     */
    public static void checkLongCompare(Long num1, Long num2, Object... msgValues) {
        if (num1 == null || num2 == null || num1 < 0 || num2 < 0) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkLongCompare", msgValues);
        }
        if (num2.compareTo(num1) == -1) {
            throw new BusinessException(ExceptionDefinition.PARAM_ERROR.code, ExceptionDefinition.PARAM_ERROR.key, "checkLongCompare", msgValues);
        }
    }

    /**
     * 若num2小于num1，抛异常
     *
     * @param num1
     * @param num2
     * @param e
     * @param msgValues
     */
    public static void checkDoubleCompare(Double num1, Double num2, BusinessException e, Object... msgValues) {
        if (num1 == null || num2 == null || num1 <= 0 || num2 <= 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
        if (num2 < num1) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 若num2小于num1，抛异常
     *
     * @param num1
     * @param num2
     * @param e
     * @param msgValues
     */
    public static void checkDoubleCompare2(Double num1, Double num2, BusinessException e, Object... msgValues) {
        if (num1 == null || num2 == null || num1 < 0 || num2 < 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
        if (num2 < num1) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 验证字符串是否以指定字符开头，不是抛异常
     *
     * @param str
     * @param begin
     * @param e
     * @param msgValues
     */
    public static void checkStringBeginChar(String str, String begin, BusinessException e, Object... msgValues) {
        if (!str.startsWith(begin)) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }

    }

    /**
     * 比较开始时间点与截止时间点的大小，若截止时间小于或等于开始时间，则抛异常
     *
     * @param startTime
     * @param endTime
     * @param e
     * @param msgValues
     */
    public static void checkTimeNotGreaterThanTime(String startTime, String endTime, BusinessException e, Object... msgValues) {
        if ((startTime.compareTo(endTime)) >= 0) {
            throw new BusinessException(e.getExceptionCode(), e.getMessageCode(), e.getMessage(), msgValues);
        }
    }

    /**
     * 判断对象是否Empty(null或元素为0)<br>
     * 实用于对如下对象做判断:String Collection及其子类 Map及其子类
     *
     * @param pObj 待检查对象
     * @return boolean 返回的布尔值
     */
    private static final boolean objectIsEmpty(Object pObj) {
        if (pObj == null) {
            return true;
        }
        if (pObj == "") {
            return true;
        }
        if (pObj instanceof String) {
            if (((String) pObj).trim().length() == 0) {
                return true;
            }
        } else if (pObj instanceof Collection<?>) {
            if (((Collection<?>) pObj).size() == 0) {
                return true;
            }
        } else if (pObj instanceof Map<?, ?>) {
            if (((Map<?, ?>) pObj).size() == 0) {
                return true;
            }
        }
        return false;
    }

}
