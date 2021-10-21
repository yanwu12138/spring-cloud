package com.yanwu.spring.cloud.common.pojo;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * @author Baofeng Xu
 * @date 2021/10/21 17:57.
 * <p>
 * description: 自定义顺序的有序List
 */
public class SortedList<T extends Comparable<T>> extends LinkedList<T> implements Serializable {
    private static final long serialVersionUID = 2441953202115391358L;

    @Override
    public boolean add(T t) {
        int index = -1;
        for (int i = 0; i < this.size(); i++) {
            T t1 = this.get(i);
            int compare = t.compareTo(t1);
            if (compare == 0) {
                return false;
            }
            if (compare < 0 && index == -1) {
                index = i;
            }
        }
        if (index == -1) {
            super.add(t);
        } else {
            super.add(index, t);
        }
        return true;
    }

}
