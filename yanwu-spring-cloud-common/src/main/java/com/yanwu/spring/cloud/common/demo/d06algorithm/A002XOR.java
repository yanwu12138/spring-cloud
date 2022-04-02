package com.yanwu.spring.cloud.common.demo.d06algorithm;

import com.yanwu.spring.cloud.common.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Baofeng Xu
 * @date 2022/3/29 15:34.
 * <p>
 * description: 异或运算
 */
@Slf4j
@SuppressWarnings("all")
public class A002XOR {

    public static void main(String[] args) {
        log.info("code1: {}", code_1(new int[]{2, 7, 3, 6, 7, 2, 1, 3, 6, 6, 2, 7, 1, 7, 6}));
        log.info("code2: {}", code_2((0B00111000100010101000100)));
        log.info("code3: {}", code_3(new int[]{2, 7, 3, 6, 7, 2, 1, 3, 6, 6, 2, 7, 1, 7, 6, 7}));
        log.info("code4: {}", code_4((0B00111000100010101000100)));
        log.info("code5: {}", code_5(new int[]{3, 3, 3, 4, 4, 4, 4, 4, 1, 1, 1, 1, 1, 7, 7, 7, 7, 7}, 3, 5));
    }

    /***
     * 描述：一个数组中有一个数出现了奇数次，其它数都出现了偶数次，找到并输出这个数。
     * 分析：
     ** 归零律：a ^ a = 0
     ** 交换律：a ^ b = b ^ a
     */
    private static int code_1(int[] arr) {
        if (arr == null || arr.length == 0) {
            throw new RuntimeException("The parameter does not meet the conditions: arr is empty.");
        }
        if (arr.length == 1) {
            return arr[0];
        }
        int eor = 0;
        for (int item : arr) {
            eor ^= item;
        }
        return eor;
    }

    /***
     * 描述：提取出一个int类型的数的最右侧的1来
     * 分析：自身 & (自身取反 + 1)
     ************* i: 00111000100010101000100
     ************ ~i: 11000111011101010111011
     ******** ~i + 1: 11000111011101010111100
     ** i & (~i + 1): 00000000000000000000100
     */
    private static int code_2(int i) {
        return NumberUtil.farRight1(i);
    }

    /***
     * 描述：一个数组中有两个数（a、b）出现了奇数次，其它数都出现了偶数次，找到并打印这两个数
     * 分析：
     ** 1、先取整个数字的异或结果：C1（异或整个数组的结果 等价与 a ^ b）
     ** 2、从 a != b 可以得知 C1 != 0
     ** 3、找到异或结果的最右侧的1：rightOne
     ** 4、将数组中所有的数分为两个部分：((i & rightOne) == 0) 和 ((i & rightOne) != 0)，a、b肯定在两个不同的部分
     ** 5、重新异或一遍这个数组，但此时只对一种部分的数参与运算：((i & rightOne) == 0) 或 ((i & rightOne) != 0)，得到C2
     ** 6、此时 C2 = a ^ C1 或 C2 = b ^ C1，而 C1 = a ^ b，所以 C2 = a ^ a ^ b = b 或者 C2 = b ^ a ^ b = a
     ** 7、已经得出a、b其中的一个就是 C2，那么另一个就是 C2 ^ C1
     */
    private static int[] code_3(int[] arr) {
        if (arr == null || arr.length <= 1) {
            throw new RuntimeException("The parameter does not meet the conditions.");
        }
        int eor1 = code_1(arr);
        if (eor1 == 0) {
            throw new RuntimeException("The parameter does not meet the conditions, eor is 0.");
        }
        int right_1 = code_2(eor1);
        int eor2 = 0;
        for (int item : arr) {
            if ((item & right_1) != 0) {
                eor2 ^= item;
            }
        }
        return new int[]{eor2, eor2 ^ eor1};
    }

    /***
     * 描述：数出一个二进制数中1的个数
     * 分析：
     ** 取出最右侧的1：right_1，每取出一个则计数+1，然后将最右侧的1抹掉
     */
    private static int code_4(int i) {
        int count = 0;
        if (i == 0) {
            return count;
        }
        while (i != 0) {
            int right_1 = code_2(i);
            // -----           i: 011011010000
            // -----     right_1: 000000010000
            // ----- i ^ right_1: 011011000000
            i ^= right_1;
            count++;
        }
        return count;
    }

    /***
     * 描述：一个数组中有一种数出现了K次，其它数都出现了M次，其中: K > 1 && K < M。找到出现了K次的数
     * 要求：时间复杂度: O(n)，额外空间复杂度: O(1)
     * 分析：
     ** 1、准备一个长度为32为的数组：sums
     ** 2、将每一个int值转换成二进制
     ** 3、循环处理这些二进制数字，判断每隔二进制数的每位是否是：1
     *** 是1：则在sums对应角标的位置 +1
     *** 不是1： 则不做处理
     ** 4、所有的数处理完后，处理最终的：sums
     ** 5、循环处理sums中的数，判断当前是元素 %m 是否为：0
     *** 是0：说明arr中出现了k次的数在转换成二进制的时候该位置必然是：0
     *** 不是0：不是0则必然为：k（如果既不是0也不是k说明流程或数组错误）
     ** 6、根据sums的循环的判断，依次给result的相对应的位置填入：1
     */
    private static int code_5(int[] arr, int k, int m) {
        if (arr == null || k < 1 || m < k || arr.length < k + m) {
            throw new RuntimeException("The parameter does not meet the conditions: arr is empty.");
        }
        int[] sums = new int[32];
        for (int item : arr) {
            for (int index = 0; index < 32; index++) {
                sums[index] += (item >> index) & 1;
            }
        }
        int result = 0;
        for (int i = 0; i < sums.length; i++) {
            int modulo = sums[i] % m;
            if (modulo == 0) {
                continue;
            }
            if (modulo != k) {
                throw new RuntimeException("The parameter does not meet the conditions: arr is empty.");
            }
            // ----- 当该位置的总数%m结果为0时, 说明该位置必然出现了m次，否则%m的结果必然为k
            result |= (1 << i);
        }
        return result;
    }

}
