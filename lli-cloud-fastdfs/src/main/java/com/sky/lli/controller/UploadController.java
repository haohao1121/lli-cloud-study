package com.sky.lli.controller;

import cn.hutool.core.util.IdUtil;
import com.luhuiguo.fastdfs.domain.MetaData;
import com.luhuiguo.fastdfs.domain.StorePath;
import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.FileIndexService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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


    /**
     * 单个文件上传
     */
    private static final String UPLOAD = "upload";

    /**
     * 批量上传
     */
    private static final String BATCH_UPLOAD = "batchUpload";

    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FileIndexService fileIndexService;

    /**
     * 方法说明: 单个文件上传
     *
     * @param file 文件
     * @return 文件信息
     * @date 2020/8/22
     * @author klaus
     */
    @PostMapping(UPLOAD)
    public ResponseResult<Object> upload(@RequestPart("file") MultipartFile file) {
        // 上传文件
        FileIndex fileIndex = uploadFile(file);
        //返回文件信息
        return ResultResponseUtils.success(fileIndex);
    }

    /**
     * 方法说明: 批量文件上传
     *
     * @param files 文件
     * @return 文件信息
     * @date 2020/8/22
     * @author klaus
     */
    @PostMapping(BATCH_UPLOAD)
    public ResponseResult<Object> batchUpload(@RequestPart("files") MultipartFile[] files) {

        //批量上传,循环调用单个上传方法
        List<FileIndex> list = Arrays.stream(files).map(this::uploadFile).collect(Collectors.toList());
        //返回文件集合信息
        return ResultResponseUtils.success(JsonUtils.toJson(list));
    }


    /**
     * 描述: 文件上传方法+MongoDB保存
     *
     * @param file 文件信息
     * @return 文件信息
     * @date 2020/8/23
     * @author klaus
     */
    private FileIndex uploadFile(MultipartFile file) {
        //文件信息
        FileIndex fileIndex = null;
        try {
            log.info(" upload file start ... ");
            // Metadata
            Set<MetaData> metaDataSet = createMetaData();
            //文件类型
            String suffix = FilenameUtils.getExtension(file.getOriginalFilename());
            // 上传文件
            StorePath storePath = this.fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), suffix, metaDataSet);

            //保存数据信息
            fileIndex = buildFileIndex(file, storePath);
            this.fileIndexService.saveFile(fileIndex);

            log.info(" upload file success , fileIndex info :{}", JsonUtils.toJson(fileIndex));
            //返回文件信息
            return fileIndex;
        } catch (Exception e) {
            log.error("test fail-", e);
            //文件上传失败,删除MongoDB数据
            if (null != fileIndex) {
                this.fileIndexService.deleteById(fileIndex.getUniqNo());
            }
        }
        return fileIndex;
    }


    /**
     * 描述: 构建文件信息
     *
     * @param file      文件
     * @param storePath 文件上传后的信息
     * @return 文件信息
     * @date 2020/8/22
     * @author klaus
     */
    private FileIndex buildFileIndex(MultipartFile file, StorePath storePath) {

        FileIndex fileIndex = new FileIndex();
        fileIndex.setUniqNo(IdUtil.fastSimpleUUID());
        fileIndex.setFileName(file.getOriginalFilename());
        fileIndex.setFileType(FilenameUtils.getExtension(file.getOriginalFilename()));
        fileIndex.setFilePath(storePath.getPath());
        fileIndex.setFullPath(storePath.getFullPath());
        fileIndex.setCreateTime(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()));
        //文件下载URL
        //文件来源
        return fileIndex;
    }

    /**
     * @date 2020/8/22
     * @author klaus
     * 方法说明: 初始化文件说明
     */
    private Set<MetaData> createMetaData() {
        Set<MetaData> metaDataSet = new HashSet<>();
        metaDataSet.add(new MetaData("Author", "lli"));
        metaDataSet.add(new MetaData("CreateDate",
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())));
        return metaDataSet;
    }
}
