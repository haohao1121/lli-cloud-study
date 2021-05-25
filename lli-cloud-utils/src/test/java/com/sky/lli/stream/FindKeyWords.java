package com.sky.lli.stream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * 描述:
 *
 * @author lihao
 * @date 2021/5/25
 */
public class FindKeyWords {

    public static void main(String[] args) throws Exception {
        //加载文件
        File file = new File("/Users/lihao/MyWorkSpace/IDEA_WORK/Shanshu/liuguoci-spp-backend/liuguoci-log/liuguoci-spp-backend-error.log");

        System.out.println("请输入关键字：（以逗号为间隔，并且关键词不能包含`符号）");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String[] keywords = input.split(",");

        long start = System.currentTimeMillis();

        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        System.out.println(Arrays.toString(keywords));

        //该map的key为关键字，value为关键字出现次数
        Map<String, Integer> map = new HashMap<>();

        String result;
        //按行读取日志文件，对每一行分别计算关键字次数，累加进map
        while ((result = bufferedReader.readLine()) != null) {
            //由于split方法有一个特性，就是会忽略待分割对象结尾的一个或多个分割符，所以如果分割符（关键词）位于一行结尾，就无法进行统计
            //在此处我们给每行文本结尾添加一个自定义结束符"`"（注意，结束符不能干扰到关键词！）
            result += "`";
            for (String keyword : keywords) {
                //通过split方法实现搜索关键字次数功能
                int count = (result.split(keyword)).length - 1;
                map.put(keyword, map.get(keyword) == null ? count : (map.get(keyword) + count));
            }
        }

        //将上面map的每个Entry存入list
        ArrayList<Map.Entry<String, Integer>> entryArrayList = new ArrayList<>(map.entrySet());

        //自定义list的比较器，根据value从大到小排列Entry元素
        entryArrayList.sort((o1, o2) -> o2.getValue() - o1.getValue());

        //打印
        for (Map.Entry<String, Integer> m : entryArrayList) {
            System.out.println(m.getKey() + ":" + m.getValue());
        }

        long end = System.currentTimeMillis();
        System.out.println("搜索耗时：" + (end - start) + " ms");

    }
}
