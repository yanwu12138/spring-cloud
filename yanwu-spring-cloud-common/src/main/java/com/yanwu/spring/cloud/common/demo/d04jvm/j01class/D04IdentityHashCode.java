package com.yanwu.spring.cloud.common.demo.d04jvm.j01class;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-03 23:00:48.
 * <p>
 * describe:
 */
public class D04IdentityHashCode {
    public static void main(String[] args) {
        Object obj = new Object();
        System.out.println(obj.hashCode());
        System.out.println(System.identityHashCode(obj));

        System.out.println("====================");
        D04IdentityHashCode code = new D04IdentityHashCode();
        System.out.println(code.hashCode());
        System.out.println(System.identityHashCode(code));

        System.out.println("====================");
        String yanwu1 = "yanwu";
        String yanwu2 = "yanwu";
        System.out.println(yanwu1.hashCode());
        System.out.println(yanwu2.hashCode());
        System.out.println(System.identityHashCode(yanwu1));
        System.out.println(System.identityHashCode(yanwu2));
    }
}
