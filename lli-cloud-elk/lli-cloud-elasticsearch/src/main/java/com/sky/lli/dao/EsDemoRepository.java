package com.sky.lli.dao;

import com.sky.lli.dao.model.Demo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/21
 */
public interface EsDemoRepository extends ElasticsearchRepository<Demo, String> {

}
