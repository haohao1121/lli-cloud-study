package com.sky.lli.service.fileindex.impl;

import com.sky.lli.dao.FileIndexDao;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.fileindex.IFileIndexService;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 创建时间：2018/四月/20
 *
 * @author klaus
 * 类名：FileIndexServiceImpl
 * 描述：文件索引信息Service
 */
@Service
public class FileIndexServiceImpl implements IFileIndexService {

    @Resource
    private FileIndexDao fileIndexDao;

    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * 保存文件索引
     *
     * @param pojo 文件索引对象
     */
    @Override
    public void insert(FileIndex pojo) {
        fileIndexDao.save(pojo);
    }

    /**
     * 根据唯一号查找
     *
     * @param uniqueNo 文件唯一号
     *
     * @return 文件信息对象
     */
    @Override
    public FileIndex getFileIndexByUniqueNo(String uniqueNo) {
        Optional<FileIndex> id = fileIndexDao.findById(uniqueNo);
        return id.orElse(null);
    }

    /**
     * 方法说明:修改文件名称
     *
     * @param fileIndex 文件信息
     *
     * @date 2020-08-14
     */
    @Override
    public void modifyFileName(FileIndex fileIndex) {
        this.mongoTemplate.findAndModify(Query.query(Criteria.where("fileUniqueId").is(fileIndex.getFileUniqueId())),
                                         Update.update("fileName", fileIndex.getFileName()), FileIndex.class);
    }
}