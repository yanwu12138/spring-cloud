package com.yanwu.spring.cloud.common.core.exception;

public interface ExceptionDefinition extends BusinessExceptionCode {

    class CodeAndKey {
        public CodeAndKey( Integer code,String key) {
            this.code = code;
            this.key = key;
        }
        public Integer      code;
        public String       key;

    }

    /*** 系统异常 */
    CodeAndKey SYSTEM_ERROR                     = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 1,"systemError");
    /*** 权限不足 */
    CodeAndKey NO_PMSN                          = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 2,"noPmsn");
    /*** 参数有误 */
    CodeAndKey PARAM_ERROR                      = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 3,"paramError");
    /*** 文件路径不存在 */
    CodeAndKey FILE_PATH_NOT_EXISTS             = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 4,"filePathNotExists");
    /*** 值小于零 */
    CodeAndKey THE_VALUE_IS_LESS_THAN_ZERO      = new CodeAndKey(EXCEPTIONCODE_YANWU_SYS_BASE - 5,"theValueIsLessThanZero");

}