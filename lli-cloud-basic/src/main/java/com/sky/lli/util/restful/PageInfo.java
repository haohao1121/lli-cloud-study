package com.sky.lli.util.restful;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author lihao
 * @date 2020/06/16
 */
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PageInfo<T> {

    /**
     * 当前页
     */
    private int pageNo;
    /**
     * 当前页记录条数
     */
    private int pageSize;
    /**
     * 总条数
     */
    private long totalCount;
    /**
     * 总页数
     */
    private long totalPages;
    /**
     * 当前页数据集合
     */
    private List<T> pageItems = new ArrayList<>();

    /**
     * 方法说明: 分页方法
     *
     * @param pageNo   当前页
     * @param pageSize 每页条数
     * @param list     要进行分页的数据
     */
    public PageInfo(int pageNo, int pageSize, List<T> list) {
        if (pageNo <= 0) {
            pageNo = 1;
        }
        List<T> pageList = list.stream().skip((pageNo - 1) * (long) pageSize).limit(pageSize)
                        .collect(Collectors.toList());
        //总条数
        int length = list.size();
        //总页数
        this.totalPages = ((length - 1) / pageSize + 1);
        //总条数
        this.totalCount = (length);
        //当前页
        this.pageNo = pageNo;
        //每页条数
        this.pageSize = pageSize;
        //分页数据
        this.pageItems = pageList;
    }
}
