package com.yanwu.spring.cloud.common.demo.d04jvm.j01class;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-02 20:18:49.
 * <p>
 * describe:
 */
public class D02ClassLoader {

    public static void main(String[] args) {
        // ----- null > Bootstrap
        System.out.println(String.class.getClassLoader());
        // ----- sun.misc.Launcher$ExtClassLoader > Extension
        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());
        // ----- sun.misc.Launcher$AppClassLoader > Application
        System.out.println(com.yanwu.spring.cloud.common.demo.d04jvm.j01class.D02ClassLoader.class.getClassLoader());
        // ----- null > Bootstrap
        System.out.println(com.yanwu.spring.cloud.common.demo.d04jvm.j01class.D02ClassLoader.class.getClassLoader().getClass().getClassLoader());
    }

}
