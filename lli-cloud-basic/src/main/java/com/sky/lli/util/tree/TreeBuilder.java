package com.sky.lli.util.tree;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述：java构建树工具类
 *
 * @author klaus
 * @date 2017/10/13
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TreeBuilder {


    /**
     * Date 2019-05-27
     * Author lihao
     * 方法说明: 构建树
     *
     * @param treeNodes 源数据
     */
    public static List<TreeNode> buildTree(List<TreeNode> treeNodes) {
        if (CollectionUtils.isEmpty(treeNodes)) {
            return new ArrayList<>();
        }
        //新的树
        List<TreeNode> treeList = new ArrayList<>();
        //获取所有的节点ID
        List<String> nodeIds = treeNodes.stream().map(TreeNode::getId).distinct().collect(Collectors.toList());
        //获取所有ID之外的父节点集合
        List<String> noFoundParentIds = treeNodes.stream().filter(item -> nodeIds.contains(item.getParentId())).map(TreeNode::getParentId).distinct().collect(Collectors.toList());

        //循环所有父节点,找出所有子集
        for (String pid : noFoundParentIds) {
            treeList.addAll(buildTree(treeNodes, pid));
        }
        return treeList;
    }

    /**
     * Date 2017/10/13
     * Author lihao [lihao@sinosoft.com.cn]
     * <p>
     * 方法说明: 使用递归方法建树
     *
     * @param treeNodes 传入的树节点列表
     * @param parentId  根节点ID
     */
    public static List<TreeNode> buildTree(List<TreeNode> treeNodes, String parentId) {
        if (CollectionUtils.isEmpty(treeNodes)) {
            return new ArrayList<>();
        }
        List<TreeNode> childList = new ArrayList<>();
        //获取所有子集
        List<TreeNode> childItem = treeNodes.stream().filter(item -> item.getParentId().equals(parentId)).collect(Collectors.toList());
        //判断子集是否为空
        if (!CollectionUtils.isEmpty(childItem)) {
            for (TreeNode node : childItem) {
                node.setChildList(buildTree(treeNodes, node.getId()));
                childList.add(node);
            }
        }
        return childList;
    }
}
