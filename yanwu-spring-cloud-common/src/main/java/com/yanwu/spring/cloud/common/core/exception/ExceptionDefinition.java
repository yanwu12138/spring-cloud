package com.yanwu.spring.cloud.common.core.exception;

import java.lang.reflect.Field;

/**
 * @author XuBaofeng.
 * @date 2018-09-29 22:11.
 * <p>
 * description: 异常常量类
 */
public interface ExceptionDefinition extends BusinessExceptionCode {

    /**
     * 判断该message是否属于 ExceptionDefinition 中的常量
     *
     * @param message
     * @return
     */
    static boolean isExist(String message) {
        Class<ExceptionDefinition> clazz = ExceptionDefinition.class;
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            try {
                Object obj = field.get(clazz);
                if (!(obj instanceof CodeAndKey)) {
                    continue;
                }
                CodeAndKey codeAndKey = (CodeAndKey) obj;
                if (codeAndKey.key.equals(message)) {
                    return true;
                }
            } catch (Exception e) {
            }
        }
        return false;
    }

    class CodeAndKey {
        public CodeAndKey(Integer code, String key) {
            this.code = code;
            this.key = key;
        }

        public Integer code;
        public String key;
    }

    /*** 系统异常 */
    CodeAndKey SYSTEM_ERROR = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 1, "systemError");
    /*** 权限不足 */
    CodeAndKey NO_PMSN = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 2, "noPmsn");
    /*** 参数有误 */
    CodeAndKey PARAM_ERROR = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 3, "paramError");
    /*** 文件路径不存在 */
    CodeAndKey FILE_PATH_NOT_EXISTS = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 4, "filePathNotExists");
    /*** 值小于零 */
    CodeAndKey THE_VALUE_IS_LESS_THAN_ZERO = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 5, "theValueIsLessThanZero");

}