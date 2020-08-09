package com.sky.lli.util.tree;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 描述：树形结构基础信息
 *
 * @author klaus
 * @date 2019-05-27
 */

@Data
public class TreeNode implements Serializable {

    /**
     * 当前ID
     */
    private String id;
    /**
     * 父节点ID
     */
    private String parentId;

    /**
     * 子集
     */
    private List<TreeNode> childList;

    /**
     * 原数据
     */
    private Object source;
}
