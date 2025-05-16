/*
 * Copyright (c) 2007-2011 Madhav Vaidyanathan
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 */


package com.weiyin.qinplus.changemiditrack;

import java.util.Arrays;

/**
 * <pre>
 *     e-mail : niejunfeng@win-electr.com
 *     time   : 2016年6月24日
 *     desc   :An ArrayList of int types.
 *     version: 1.0.5
 *     Copyright: Copyright（c）2017
 *     Company:上海蔚音电子科技有限公司
 * </pre>
 *
 * @author niejunfeng
 */
public class ListInt {
    private int[] data;
    /**
     * The list of ints
     */
    private int count;

    /**
     * The size of the list
     */

    public ListInt() {
        data = new int[11];
        count = 0;
    }

    public ListInt(int capacity) {
        data = new int[capacity];
        count = 0;
    }

    public int size() {
        return count;
    }

    public void add(int x) {
        if (data.length == count) {
            int[] newData = new int[count * 2];
            System.arraycopy(data, 0, newData, 0, count);
            data = newData;
        }
        data[count] = x;
        count++;
    }

    public int get(int index) {
        return data[index];
    }

    public void set(int index, int x) {
        data[index] = x;
    }

    public boolean contains(int x) {
        for (int i = 0; i < count; i++) {
            if (data[i] == x) {
                return true;
            }
        }
        return false;
    }


    public void sort() {
        Arrays.sort(data, 0, count);
    }
}

