package com.sky.lli.controller;

import com.luhuiguo.fastdfs.domain.MetaData;
import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.util.json.JsonUtils;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/22
 */

@Slf4j
@RestController
@RequestMapping("/upload")
public class UploadController {


    private static final String UPLOAD = "upload";

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 方法说明: 单个文件上传
     *
     * @param file 文件
     * @return 文件信息
     * @date 2020/8/22
     * @author klaus
     */
    @PostMapping(UPLOAD)
    public ResponseResult<Object> uploadFile(@RequestPart("file") MultipartFile file) {
        log.info("开始上传文件");

        try {
            // Metadata
            Set<MetaData> metaDataSet = createMetaData();
            //文件类型
            String suffix = FilenameUtils.getExtension(file.getOriginalFilename());
            // 上传文件
            StorePath storePath = this.fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, metaDataSet);

            log.info("文件上传成功,文件信息:{}", JsonUtils.toJson(storePath));
            //返回文件信息
            return ResultResponseUtils.success(storePath);
        } catch (Exception e) {
            log.error("test fail-", e);
            return ResultResponseUtils.error(ExceptionEnum.SYS_INVOKING_ERROR);
        }
    }

    /**
     * @date 2020/8/22
     * @author klaus
     * 方法说明: 初始化文件说明
     */
    private Set<MetaData> createMetaData() {
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("Author", "lli"));
        metaDataSet.add(new MetaData("CreateDate", new Date().toString()));
        return metaDataSet;
    }
}
