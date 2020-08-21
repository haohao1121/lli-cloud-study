package com.sky.lli.util;

import com.sky.lli.util.json.JsonUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * 描述: 集合工具类
 *
 * @author klaus
 * @date 2020-01-01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CollectionUtil implements Serializable {

    private static Logger log = LoggerFactory.getLogger(CollectionUtil.class);

    /**
     * 方法说明 : 判断 map 是否为空
     *
     * @author klaus
     * @date 2017/8/3
     */
    public static boolean mapIsNull(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 方法说明 : 把集合拆成最大容量为指定值的集合
     *
     * @param <T>             泛型T
     * @param list            要拆分的集合
     * @param maxCountPerList 单个集合中元素数量
     *
     * @return 返回封装拆分后集合的集合
     *
     * @author klaus
     * @date 2017/10/10
     */
    public static <T> List<List<T>> splitList(List<T> list, int maxCountPerList) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>(0);
        }

        int len = list.size();
        // 判断需要拆分成几个集合
        int size = len % maxCountPerList;
        if (size == 0) {
            size = len / maxCountPerList;
        } else {
            size = (len / maxCountPerList) + 1;
        }
        List<List<T>> packList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int fromIndex = i * maxCountPerList;
            int toIndex = Math.min(fromIndex + maxCountPerList, len);

            packList.add(list.subList(fromIndex, toIndex));
        }

        return packList;
    }

    /**
     * 方法说明 : 把集合拆成最大容量为指定值的List集合
     *
     * @param <T>        泛型T
     * @param collection 传入的集合
     * @param count      限制参数的个数
     *
     * @return 返回封装拆分后集合的集合
     *
     * @author klaus
     * @date 2017/10/10
     */
    public static <T> List<List<T>> splitList(Collection<T> collection, int count) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>(0);
        }

        List<T> list = new ArrayList<>(collection.size());
        list.addAll(collection);

        return splitList(list, count);
    }

    /**
     * 方法说明 : 把数组拆成最大容量为指定值的List集合
     *
     * @param <T>   泛型T
     * @param objs  传入的数组
     * @param count 限制参数的个数
     *
     * @return 返回封装拆分后集合的集合
     *
     * @author klaus
     * @date 2017/10/10
     */
    public static <T> List<List<T>> splitList(T[] objs, int count) {
        if (objs == null || objs.length == 0) {
            return new ArrayList<>(0);
        }

        List<T> list = Arrays.asList(objs);

        return splitList(list, count);
    }

    /**
     * 方法说明 : 把Map集合拆成最大容量为指定值的Map集合
     *
     * @param <K>   泛型T
     * @param <V>   泛型V
     * @param map   传入的集合
     * @param count 限制参数的个数
     *
     * @return 返回封装拆分后集合的集合
     *
     * @author klaus
     * @date 2017/10/10
     */
    public static <K, V> List<Map<K, V>> splitMap(Map<K, V> map, int count) {
        if (map == null || map.size() == 0) {
            return new ArrayList<>(0);
        }

        Set<Entry<K, V>> entrySet = map.entrySet();
        List<List<Entry<K, V>>> list = splitList(entrySet, count);
        List<Map<K, V>> mapList = new ArrayList<>(list.size());
        for (List<Entry<K, V>> entryList : list) {
            Map<K, V> mapMin = new HashMap<>(entryList.size());
            for (Entry<K, V> entry : entryList) {
                mapMin.put(entry.getKey(), entry.getValue());
            }
            mapList.add(mapMin);
        }
        return mapList;
    }

    /**
     * @param list 要克隆的数据集合
     * @param <T>  泛型
     *
     * @date 2017/10/10
     * @author klaus
     * <p>
     * 方法说明: list深度克隆
     */
    @Deprecated
    @SuppressWarnings("unchecked")
    public static <T> List<T> deepCopy(List<T> list) {
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(list);

            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            ObjectInputStream in = new ObjectInputStream(byteIn);
            @SuppressWarnings("unchecked") List<T> dest = (List<T>) in.readObject();
            return dest;
        } catch (IOException | ClassNotFoundException e) {
            log.error("list深度克隆出错,错误信息如下:{}", e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * @param list     集合
     * @param classOfT 类型
     *
     * @return 新的集合
     *
     * @date 2018/9/5
     * @author klaus
     * 方法说明: Json序列化深度拷贝List集合
     */
    public static <T> List<T> deepCopyList(List<T> list, Class<T> classOfT) {
        if (!CollectionUtils.isEmpty(list)) {
            //序列化
            String jsonList = JsonUtils.toJson(list);
            //反序列化
            return JsonUtils.fromJsonList(jsonList, classOfT);
        }
        return new ArrayList<>();
    }

    /**
     * @param list 集合
     * @param <T>  泛型
     *
     * @return 返回去重后的list集合
     *
     * @date 2018/1/10
     * @author klaus
     * 方法说明: 删除重复数据,返回新的集合
     */
    public static <T> List<T> removeDuplicate(List<T> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return new ArrayList<>(new HashSet<>(list));
    }

    /**
     * @param str 要转换的字符串
     *
     * @return 返回转换后的list集合
     *
     * @date 2017/1/3
     * @author klaus
     * <p>
     * 方法说明: 将字符串转换为指定类型的list集合
     */
    public static List<Integer> getIntegerList(String str) {
        if (StringUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

    /**
     * @param str 要转换的字符串
     *
     * @return 返回转换后的list集合
     *
     * @date 2017/1/3
     * @author klaus
     * <p>
     * 方法说明: 将字符串转换为指定类型的list集合
     */
    public static List<Long> getLongList(String str) {
        if (StringUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(",")).map(Long::parseLong).collect(Collectors.toList());
    }

    /**
     * @param str 要转换的字符串
     *
     * @return 返回转换后的list集合
     *
     * @date 2017/1/3
     * @author klaus
     * <p>
     * 方法说明: 将字符串转换为指定类型的list集合
     */
    public static List<String> getStringList(String str) {
        if (StringUtils.isEmpty(str)) {
            return new ArrayList<>();
        }
        return Arrays.asList(str.split(","));
    }

    /**
     * 构建List dto转换为vo
     *
     * @param data  service 返回的dto集合
     * @param clazz 返出VO class
     */
    public static <T> List<T> builderList(List<?> data, Class<T> clazz) {
        if (!CollectionUtils.isEmpty(data)) {
            if (clazz != null) {
                int size = data.size();
                List<T> voList = new ArrayList<>(size + (size / 2));
                data.forEach(item -> {
                    try {
                        T o = clazz.newInstance();
                        BeanUtils.copyProperties(item, o);
                        voList.add(o);
                    } catch (Exception e) {
                        log.error("执行对象Copy出现错误" + e.getMessage(), e);
                    }
                });
                return voList;
            }
        }
        return new ArrayList<>();
    }

    /**
     * 构建Object dto转换为vo
     *
     * @param data  service 返回的dto对象
     * @param clazz 返出VO class
     */
    public static <T> T builderObject(Object data, Class<T> clazz) {
        if (data != null) {
            if (clazz != null) {
                try {
                    T o = clazz.newInstance();
                    BeanUtils.copyProperties(data, o);
                    return o;
                } catch (Exception e) {
                    log.error("执行对象Copy出现错误" + e.getMessage(), e);
                }
            }
        }
        return null;
    }

}
