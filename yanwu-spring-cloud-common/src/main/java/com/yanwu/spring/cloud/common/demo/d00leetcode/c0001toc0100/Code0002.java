package com.yanwu.spring.cloud.common.demo.d00leetcode.c0001toc0100;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Baofeng Xu
 * @date 2020/8/14 11:19.
 * <p>
 * description:
 * 给出两个 非空 的链表用来表示两个非负的整数。其中，它们各自的位数是按照 逆序 的方式存储的，并且它们的每个节点只能存储 一位 数字。
 * 如果，我们将这两个数相加起来，则会返回一个新的链表来表示它们的和。
 * 您可以假设除了数字 0 之外，这两个数都不会以 0 开头。
 * <p>
 * 示例：
 * 输入：(2 -> 4 -> 3) + (5 -> 6 -> 4)
 * 输出：7 -> 0 -> 8
 * 原因：342 + 465 = 807
 * <p>
 * 来源：力扣（LeetCode）
 * 链接：https://leetcode-cn.com/problems/add-two-numbers
 * 著作权归领扣网络所有。商业转载请联系官方授权，非商业转载请注明出处。
 */
public class Code0002 {

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        l1 = reverse(l1);
        l2 = reverse(l2);
        return null;
    }

    public static void main(String[] args) {
        ListNode listNode1 = new ListNode(2);

        ListNode listNode2 = new ListNode(4);

        ListNode listNode3 = new ListNode(3);

        ListNode listNode4 = new ListNode(8);
        listNode3.next = listNode4;
        listNode2.next = listNode3;
        listNode1.next = listNode2;

        ListNode reverse = reverse(listNode1);
        System.out.println(reverse);
    }

    private static ListNode reverse(ListNode source) {
        ListNode result = new ListNode();
        while (source != null) {
            ListNode next = source.next;
            source.next = result.next;
            result.next = source;
            source = next;
        }
        return result.next;
    }

    @Data
    @NoArgsConstructor
    static class ListNode {
        private int num;
        private ListNode next;

        public ListNode(int num) {
            this(num, null);
        }

        public ListNode(int num, ListNode next) {
            this.num = num;
            this.next = next;
        }
    }

}
