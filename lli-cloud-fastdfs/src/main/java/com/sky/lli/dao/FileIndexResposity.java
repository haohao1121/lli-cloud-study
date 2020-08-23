package com.sky.lli.dao;

import com.sky.lli.model.FileIndex;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/22
 */
@Repository
public interface FileIndexResposity extends MongoRepository<FileIndex, String> {
}
