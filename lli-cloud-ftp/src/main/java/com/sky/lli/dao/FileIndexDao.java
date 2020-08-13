package com.sky.lli.dao;

import com.sky.lli.model.FileIndex;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/14
 */
public interface FileIndexDao extends MongoRepository<FileIndex, String> {

}
