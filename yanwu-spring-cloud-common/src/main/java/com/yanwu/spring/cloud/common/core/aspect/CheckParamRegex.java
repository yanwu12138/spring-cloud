package com.yanwu.spring.cloud.common.core.aspect;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-02 14:39.
 * <p>
 * description:
 */
public interface CheckParamRegex {
    /*** 身份证 */
    String CARD_NO = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)";
    /*** 手机号 */
    String PHONE_NO = "^(13[0-9]|14[5|7]|15[0|1|2|3|4|5|6|7|8|9]|18[0|1|2|3|5|6|7|8|9])\\d{8}$";
    /*** 邮箱 */
    String EMAIL = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    /*** 字符串不全为空格 */
    String STRING_NOT_NULL = "^[^ ]+$";
    /*** Boolean不能为null ***/
    String BOOLEAN_NOT_NULL = "^true|false$";
    /*** Long不能为null ***/
    String LONG_NOT_NULL = "^[0-9]*$";
    /*** 验证码 ***/
    String CAPTCHA = "^[0-9]{6}$";
    /*** 密码 ***/
    String PASSWORD = "^[a-zA-Z0-9]{6,12}$";
}
