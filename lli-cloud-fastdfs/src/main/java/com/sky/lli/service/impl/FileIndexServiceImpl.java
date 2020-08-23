package com.sky.lli.service.impl;

import com.sky.lli.dao.FileIndexResposity;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.FileIndexService;
import lombok.Data;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/22
 */
@Data
@Service
public class FileIndexServiceImpl implements FileIndexService {

    @Resource
    private FileIndexResposity fileIndexResposity;

    /**
     * 方法说明: 保存文件
     *
     * @param fileIndex 文件
     * @date 2020/8/22
     * @author klaus
     */
    @Override
    public void saveFile(FileIndex fileIndex) {
        this.fileIndexResposity.insert(fileIndex);
    }

    /**
     * 描述: 根据文件ID删除数据
     *
     * @param uniqNo 文件ID
     * @date 2020/8/22
     * @author klaus
     */
    @Override
    public void deleteById(String uniqNo) {
        this.fileIndexResposity.deleteById(uniqNo);
    }

    /**
     * 描述: 根据文件ID获取文件信息
     *
     * @param uniqNo 文件唯一号
     * @return 文件信息
     * @date 2020/8/22
     * @author klaus
     */
    @Override
    public FileIndex findFileByUniqNo(String uniqNo) {
        Optional<FileIndex> indexOptional = this.fileIndexResposity.findById(uniqNo);
        return indexOptional.orElse(null);
    }

    /**
     * 描述: 获取文件几何
     *
     * @param param 参数
     * @return 文件集合
     * @date 2020/8/22
     * @author klaus
     */
    @Override
    public List<FileIndex> findFileList(FileIndex param) {
        //查询条件
        Example<FileIndex> example = buildExample(param);
        //排序
        Sort sort = Sort.by(Sort.Direction.DESC, "createTime");

        return this.fileIndexResposity.findAll(example, sort);
    }

    /**
     * 描述: 构建查询适配器
     *
     * @param param 参数
     * @date 2020/8/22
     * @author klaus
     */
    private Example<FileIndex> buildExample(FileIndex param) {
        //创建匹配器，即如何使用查询条件
        ExampleMatcher matcher = ExampleMatcher.matching()
                //改变默认字符串匹配方式：模糊查询
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING)
                //改变默认大小写忽略方式：忽略大小写
                .withIgnoreCase(true)
                //fileName 采用“包含匹配”的方式查询
                .withMatcher("fileName", ExampleMatcher.GenericPropertyMatchers.contains())
                //fileType 采用“精准匹配”的方式查询
                .withMatcher("fileType", ExampleMatcher.GenericPropertyMatchers.exact())
                //fileSource 采用“精准匹配”的方式查询
                .withMatcher("fileSource", ExampleMatcher.GenericPropertyMatchers.exact())
                //忽略属性，不参与查询
                .withIgnorePaths("downloadUrl");

        return Example.of(param, matcher);
    }

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
    @Override
    public Page<FileIndex> findFilePage(Integer pageNo, Integer pageSize, FileIndex param) {

        //排序
        Sort sort = Sort.by(Sort.Direction.DESC, "fileSource", "createTime");
        //分页条件
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);
        //查询条件
        Example<FileIndex> example = buildExample(param);

        return this.fileIndexResposity.findAll(example, pageable);
    }
}
