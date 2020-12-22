package com.sky.lli.controller;

import com.sky.lli.dao.EsDemoRepository;
import com.sky.lli.dao.model.Demo;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/12/21
 */

@RestController
@RequestMapping("demo")
public class TestController {

    @Resource
    private ElasticsearchRestTemplate elasticsearchTemplate;
    @Resource
    private EsDemoRepository esDemoRepository;

    private Pageable pageable = PageRequest.of(0, 10);

    @PostMapping("test")
    public ResponseResult<Object> test(@RequestBody Demo demo) {

        this.elasticsearchTemplate.deleteIndex(Demo.class);

        this.esDemoRepository.save(demo);

        Demo demo1 = this.esDemoRepository.findById(demo.getId()).orElse(null);

        Page<Demo> all = this.esDemoRepository.findAll(pageable);

        return ResultResponseUtils.success(all);
    }

}
