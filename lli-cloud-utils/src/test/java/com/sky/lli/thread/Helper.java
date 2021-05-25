package com.sky.lli.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 描述: 编写两个线程，一个线程打印1~25，另一个线程打印字母A~Z，打印顺序为12A34B56C……5152Z，要求使用线程间的通信
 *
 * @author lihao
 * @date 2021/5/25
 */
public enum Helper {

    instance;

    private static final ExecutorService tPool = Executors.newFixedThreadPool(2);

    public static String[] buildNoArr(int max) {
        String[] noArr = new String[max];
        for (int i = 0; i < max; i++) {
            noArr[i] = Integer.toString(i + 1);
        }
        return noArr;
    }

    public static String[] buildCharArr(int max) {
        String[] charArr = new String[max];
        //ascii 65 - 90 代表 A-Z
        int tmp = 65;
        for (int i = 0; i < max; i++) {
            charArr[i] = String.valueOf((char) (tmp + i));
        }
        return charArr;
    }

    public static void print(String... input) {
        if (input == null)
            return;
        for (String each : input) {
            System.out.print(each);
        }
    }

    public void run(Runnable r) {
        tPool.submit(r);
    }

    public void shutdown() {
        tPool.shutdown();
    }

}
