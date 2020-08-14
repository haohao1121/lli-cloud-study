package com.sky.lli.service.fileindex;

import com.sky.lli.model.FileIndex;

/**
 * 文件索引操作
 *
 * @author klaus
 */
public interface IFileIndexService {

    /**
     * 保存文件索引
     *
     * @param fileIndex 文件索引对象
     */
    void insert(FileIndex fileIndex);

    /**
     * 根据唯一号查找
     *
     * @param uniqueNo 文件唯一号
     *
     * @return 文件信息对象
     */
    FileIndex getFileIndexByUniqueNo(String uniqueNo);

    /**
     * 方法说明:修改文件名称
     *
     * @param fileIndex 文件信息
     *
     * @date 2020-08-14
     */
    void modifyFileName(FileIndex fileIndex);
}
