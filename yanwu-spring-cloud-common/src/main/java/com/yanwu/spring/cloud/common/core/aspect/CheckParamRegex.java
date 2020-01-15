package com.yanwu.spring.cloud.common.core.aspect;

/**
 * @author <a herf="mailto:yanwu0527@163.com">XuBaofeng</a>
 * @date 2019-12-02 14:39.
 * <p>
 * description:
 */
public interface CheckParamRegex {
    /*** 身份证 */
    String CARD_NO = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)| (^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
    /*** 手机号 */
    String PHONE_NO = "^((13[0-9])|(14[5|7])|(15([0-3,5]|[5-9]))|(18[0,1,2,5-9])|(177))\\d{8}$";
    /*** 邮箱 */
    String EMAIL = "\\w+@([\\w]+[\\w-]*)(\\.[\\w]+[-\\w]*)+";
    /*** 字符串不全为空格 */
    String STRING_NOT_NULL = "^[^ ]+$";
    /*** 验证码 ***/
    String CAPTCHA = "^[0-9]{6}$";
    /*** 密码 ***/
    String PASSWORD = "^[a-zA-Z0-9]{6,12}$";
}
