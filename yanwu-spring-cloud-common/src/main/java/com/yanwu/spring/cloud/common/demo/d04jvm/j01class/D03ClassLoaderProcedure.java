package com.yanwu.spring.cloud.common.demo.d04jvm.j01class;

/**
 * @author <a href="mailto:yanwu0527@163.com">baofeng Xu</a>
 * @date 2020-06-03 20:25:33.
 * <p>
 * describe:
 * 类加载时静态你成员变量的赋值过程：
 * * 一：loadClass（类加载）
 * *    1. 将class对象加载到内存中
 * *    2. 给class对象的静态成员变量赋默认值
 * *    3. 给class对象的静态成员变量赋初始值
 * * 二：newObject（创建对象）
 * *    1. 给Object对象申请内存空间
 * *    2. 将Object对象加载到内存
 * *    3. 给Object对象的静态成员变量赋默认值
 * *    4. 给Object对象的静态成员变量赋初始值
 */
public class D03ClassLoaderProcedure {

    public static void main(String[] args) {
        // ----- 结果为：3
        System.out.println("count01 -- " + Count_01.count);
        // ----- 结果为：2
        System.out.println("count02 -- " + Count_02.count);
    }

    /**
     * 1. 将 Count_01 对象加载到内存
     * 2. 给 count 属性赋默认值：0
     * 3. 给 count_01 属性赋默认值：null
     * 4. 给 count 属性赋初始值：2
     * 5. 给 count_01 属性对象赋初始值：new Count_01()
     * 注意：此时的 count 值为：2，在第 5 步给 count_01 属性赋初始值时会调用 Count_01 的构造方法
     * 所以：count 会 ++，最终 count 值为：3
     */
    private static class Count_01 {
        public static int count = 2;
        public static Count_01 count_01 = new Count_01();

        private Count_01() {
            count++;
        }
    }

    /**
     * 1. 将 Count_02 对象加载到内存
     * 2. 给 count_02 属性赋默认值：null
     * 3. 给 count 属性赋默认值：0
     * 4. 给 count_02 属性赋初始值：new Count_02()
     * 5. 给 count 属性赋默认值：2
     * 注意：在第 4 步给 count_02 属性赋初始值时会调用 Count_02 的构造方法，此时的 count 还没有赋初始值，只有默认值，所以此时 count为：0，count++为：1
     * 在第 5 步给count 属性赋默认值时会用 2 覆盖 count++：1 的值，所以 count 最终值为：2
     */
    private static class Count_02 {
        public static Count_02 count_02 = new Count_02();
        public static int count = 2;

        private Count_02() {
            count++;
        }
    }
}
