package com.sky.lli.thread;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 描述: AtomicInteger 原子性
 *
 * @author lihao
 * @date 2021/5/25
 */
public class MethodFive {

    private AtomicInteger threadToGo = new AtomicInteger(1);

    public Runnable newThreadOne() {
        final String[] inputArr = Helper.buildNoArr(52);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (int i = 0; i < arr.length; i = i + 2) {
                    while (threadToGo.get() == 2) {
                    }
                    Helper.print(arr[i], arr[i + 1]);
                    threadToGo.set(2);
                }
            }
        };
    }

    public Runnable newThreadTwo() {
        final String[] inputArr = Helper.buildCharArr(26);
        return new Runnable() {
            private String[] arr = inputArr;

            public void run() {
                for (String s : arr) {
                    while (threadToGo.get() == 1) {
                    }
                    Helper.print(s);
                    threadToGo.set(1);
                }
            }
        };
    }

    public static void main(String[] args) {
        MethodFive five = new MethodFive();
        Helper.instance.run(five.newThreadOne());
        Helper.instance.run(five.newThreadTwo());
        Helper.instance.shutdown();
    }
}
