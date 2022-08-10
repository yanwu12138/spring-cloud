package com.yanwu.spring.cloud.common.utils;

import lombok.Getter;

/**
 * @author Baofeng Xu
 * @date 2022/5/23 18:01.
 * <p>
 * description:
 */
public class SystemUtil {

    private SystemUtil() {
        throw new UnsupportedOperationException("SystemUtil should never be instantiated");
    }

    public static SystemType getSystemType() {
        String sysType = System.getProperty("os.name").toUpperCase();
        return SystemType.getInstance(sysType);
    }

    public static boolean isWindows() {
        return SystemType.WINDOWS.equals(getSystemType());
    }

    @Getter
    public enum SystemType {
        // ----- 操作系统类
        WINDOWS("WINDOWS"),
        MAC_OS("MAC"),
        LINUX("LINUX", "UNIX"),
        ;
        private final String[] osName;

        SystemType(String... osName) {
            if (osName == null || osName.length == 0) {
                throw new UnsupportedOperationException();
            }
            this.osName = osName;
        }

        public static SystemType getInstance(String sysType) {
            for (SystemType value : SystemType.values()) {
                for (String osName : value.getOsName()) {
                    if (sysType.contains(osName)) {
                        return value;
                    }
                }
            }
            return null;
        }
    }
}
