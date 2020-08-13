package com.sky.lli.service.fileindex;


import com.sky.lli.model.FileIndex;

/**
 * 文件索引操作
 *
 * @author nicai
 */
public interface IFileIndexService {

    /**
     * 保存文件索引
     *
     * @param pojo 文件索引对象
     */
    void insert(FileIndex pojo);

    /**
     * 根据唯一号查找
     *
     * @param uniqueNo 文件唯一号
     * @return 文件信息对象
     */
    FileIndex getFileIndexByUniqueNo(String uniqueNo);

    /**
     * 修改文件名称
     */
    void modifyFileName(FileIndex fileIndex);
}
