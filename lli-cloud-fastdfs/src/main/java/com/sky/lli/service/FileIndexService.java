package com.sky.lli.service;

import com.sky.lli.model.FileIndex;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/22
 */
public interface FileIndexService {

    /**
     * 方法说明: 保存文件
     *
     * @param fileIndex 文件
     * @date 2020/8/22
     * @author klaus
     */
    void saveFile(FileIndex fileIndex);

    /**
     * 描述: 根据文件ID删除数据
     *
     * @param uniqNo 文件ID
     * @date 2020/8/22
     * @author klaus
     */
    void deleteById(String uniqNo);

    /**
     * 描述: 根据文件ID获取文件信息
     *
     * @param uniqNo 文件唯一号
     * @return 文件信息
     * @date 2020/8/22
     * @author klaus
     */
    FileIndex findFileByUniqNo(String uniqNo);

    /**
     * 描述: 获取文件几何
     *
     * @param param 参数
     * @return 文件集合
     * @date 2020/8/22
     * @author klaus
     */
    List<FileIndex> findFileList(FileIndex param);

    /**
     * 描述: 文件分页查询
     *
     * @param pageNo   当前页
     * @param pageSize 每页条数
     * @param param    参数
     * @return 文件分页信息
     * @date 2020/8/22
     * @author klaus
     */
    Page<FileIndex> findFilePage(Integer pageNo, Integer pageSize, FileIndex param);
}
