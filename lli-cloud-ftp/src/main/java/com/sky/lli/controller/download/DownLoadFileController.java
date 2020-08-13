package com.sky.lli.controller.download;

import com.alibaba.fastjson.JSON;
import com.sinosoft.cpyy.model.constant.ExceptionEnum;
import com.sinosoft.cpyy.model.fileindex.FileIndex;
import com.sinosoft.cpyy.service.filedownload.IFileDownLoad;
import com.sinosoft.cpyy.service.fileindex.IFileIndexService;
import com.sinosoft.cpyy.util.FileServerUUIDUtil;
import com.sinosoft.cpyy.util.filecompress.ApacheCompress;
import com.sinosoft.cpyy.util.voutils.ResponseResult;
import com.sinosoft.cpyy.util.voutils.ResultResponseUtils;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * 描述：文件上传Controller
 *
 * @author nicai
 */
@RestController
@Slf4j
public class DownLoadFileController {
    /**
     * 单个文件下载文件到临时目录
     */
    private static final String DOWNLOAD_TO_TEMP = "downloadtotemp";
    /**
     * 批量下载永久存储的文件
     */
    private static final String BATCH_DOWNLOAD = "batchdownload";

    /**
     * 文件唯一号ID长度
     */
    private static final Integer FILE_UNIQUE_NO_LENGTH = 32;
    /**
     * 批量下载文件生成的链接Map中的值
     */
    private static final String BATCH_DOWNLOAD_LINK = "downloadlink";
    /**
     * 批量下载文件失败的文件唯一号
     */
    private static final String BATCH_DOWNLOAD_FAIL_ITEM = "downloadfailitem";

    /**
     * 文件索引维护服务
     */
    @Resource
    private IFileIndexService fileIndexService;

    /**
     * 文件上传实现
     */
    @Resource
    private IFileDownLoad fileDownLoad;

    /**
     * 文件上传过来的时候临时目录
     */
    @Value("${fileStorage.tempPath}")
    @Setter
    private String tempPath;

    /**
     * 文件临时下载目录
     */
    @Value("${fileStorage.tempDownloadPath}")
    @Setter
    private String tempDownloadPath;

    /**
     * 文件永久下载目录
     */
    @Value("${fileStorage.permDownloadPath}")
    @Setter
    private String permDownloadPath;

    /**
     * 文件批量下载目录
     */
    @Value("${fileStorage.batchDownloadPath}")
    @Setter
    private String batchDownloadPath;

    /**
     * 描述：下载文件到临时目录(用户下载)
     *
     * @param fileUniqueNo 文件唯一号
     * @return ResponseResult
     */
    @GetMapping(DOWNLOAD_TO_TEMP)
    public ResponseResult<Object> downloadToTemp(String fileUniqueNo) {

        //检查参数是否满足
        if (!StringUtils.isNotBlank(fileUniqueNo) || fileUniqueNo.length() <= FILE_UNIQUE_NO_LENGTH) {
            return ResultResponseUtils.error(ExceptionEnum.DOWNLOAD_UNIQUENO_PARMA_IS_NULL);
        }

        //查询文件索引服务
        FileIndex fileIndexByUniqueNo = fileIndexService.getFileIndexByUniqueNo(fileUniqueNo);
        if (fileIndexByUniqueNo == null) {
            return ResultResponseUtils.error(ExceptionEnum.TEMP_DOWNLOAD_FILE_IS_NOT_EXIST);
        }

        //检测下载临时目录是否存在
        File file1 = new File(tempDownloadPath);
        if (!(file1.exists() || file1.mkdirs())) {
            return ResultResponseUtils.error(ExceptionEnum.TEMP_DOWNLOAD_CREATE_EXCEPTION);
        }

        //检查是临时下载目录是否存在文件
        String downloadPath = tempDownloadPath +
                fileUniqueNo + File.separator +
                fileIndexByUniqueNo.getFileName();
        if (!new File(downloadPath).exists()) {
            //存放临时下载目录
            File file = new File(tempPath + fileUniqueNo);
            //检查临时目录是否存在文件
            if (!file.exists()) {
                fileDownLoad.downloadFile(fileUniqueNo, fileIndexByUniqueNo.getFtpPath());
            }

            //将文件复制到临时下载区
            downloadPath = copyTempDownloadFromTemp(fileUniqueNo, downloadPath, file);
        }

        if (!StringUtils.isNotBlank(downloadPath)) {
            return ResultResponseUtils.error(ExceptionEnum.TEMP_DOWNLOAD_SUB_TEMP_PATH_EXCEPTION);
        }

        //应当返回相对路径而不是直接使用copyTempDownloadFromTemp()的返回的绝对路径
        int tempPathIndex = downloadPath.indexOf("tempDownloadPath");
        //截取到相对路径
        String resultTempPath = downloadPath.substring(tempPathIndex, downloadPath.length());
        return ResultResponseUtils.success(resultTempPath);

    }

    /**
     * 描述：批量下载永久存储的文件
     *
     * @param fileUniqueNos 文件唯一号
     * @return ResponseResult
     */
    @PostMapping(BATCH_DOWNLOAD)
    public ResponseResult<Object> batchDownload(@RequestParam String fileUniqueNos, @RequestParam(required = false) String fileName) {
        //处理开始时间
        final long starTime = System.currentTimeMillis();
        List<?> fileUniqueNoList = JSON.parseObject(fileUniqueNos, List.class);
        //成功响应data中存放的数据
        Map<String, Object> resultBatch = new HashMap<>();
        //处理失败批量下载文件的唯一号
        List<String> batchDownloadFailList = new ArrayList<>();

        //检查参数是否满足
        if (fileUniqueNoList.isEmpty()) {
            return ResultResponseUtils.error(ExceptionEnum.BATCH_DOWNLOAD_UNIQUENO_PARMA_IS_NULL);
        }

        //生成本次下载对应的下载空间目录
        String uuid = FileServerUUIDUtil.getUUID();
        if (!new File(batchDownloadPath + uuid).mkdirs()) {
            return ResultResponseUtils.error(ExceptionEnum.BATCH_DOWNLOAD_CREATE_SPACE);
        }
        if (isBlank(fileName)) {
            fileName = uuid;
        }

        //将需要下载的文件准备至下载空间
        AtomicInteger atomicInteger = new AtomicInteger(0);
        fileUniqueNoList.stream()
                .map(String::valueOf)
                .forEach(item -> {
                    //查询文件索引服务
                    FileIndex fileIndexByUniqueNo = fileIndexService.getFileIndexByUniqueNo(item);
                    //数据记录中不存在则添加此唯一id到失败列表中，反之进行下一步
                    if (fileIndexByUniqueNo == null) {
                        batchDownloadFailList.add(item);
                    } else {
                        //数据记录中存在则查询下载空间中不存在则检查缓存中是否存在
                        String filePath = batchDownloadPath + uuid + File.separator + fileIndexByUniqueNo.getFileName();
                        File fileInSpace = new File(filePath);
                        //如果文件已经存在则应当重命名
                        if (fileInSpace.exists()) {
                            //获取文件扩展名
                            String extension = FilenameUtils.getExtension(fileInSpace.getName());
                            //获取基础名称
                            String baseName = FilenameUtils.getBaseName(fileInSpace.getName());
                            fileInSpace = new File(fileInSpace.getParent() +
                                    File.separator +
                                    baseName + '(' + atomicInteger.incrementAndGet() + ')' + FilenameUtils.EXTENSION_SEPARATOR +
                                    extension);
                        }
                        downloadToSpace(item, fileIndexByUniqueNo, fileInSpace);
                    }
                });

        //压缩空间并返回
        File[] files = new File(batchDownloadPath + uuid).listFiles();
        if (files != null) {
            resultBatch.put(BATCH_DOWNLOAD_LINK, ApacheCompress.compressFilesToZip(Arrays.asList(files), batchDownloadPath + uuid + File.separator + FilenameUtils.getBaseName(fileName)));
        }

        //失败的文件放入批处理失败的返回中
        resultBatch.put(BATCH_DOWNLOAD_FAIL_ITEM, batchDownloadFailList);

        //对存在下载用时统计
        log.info("本次批量下载用时:{},失败唯一号{},所有文件唯一号{}",
                (System.currentTimeMillis() - starTime),
                JSON.toJSONString(batchDownloadFailList),
                JSON.toJSONString(fileUniqueNoList));

        //修正下载链接与文件物理路径关系
        String zipPath = (String) resultBatch.get(BATCH_DOWNLOAD_LINK);
        resultBatch.computeIfPresent(BATCH_DOWNLOAD_LINK, (key, value) ->
                zipPath.substring(zipPath.indexOf("batchDownloadPath"), zipPath.length()));

        //检查生成结果并返回(链接是否生成)
        if (resultBatch.get(BATCH_DOWNLOAD_LINK) == null) {
            return ResultResponseUtils.error(ExceptionEnum.BATCH_DOWNLOAD_ZIP_FAIL);
        }
        return ResultResponseUtils.success(JSON.toJSONString(resultBatch));
    }


    //============================================私有=======================================================

    /**
     * 下载原始文件并转换为新开辟的存储的文件
     *
     * @param item                文件唯一号
     * @param fileIndexByUniqueNo 数据库中的模型对象
     * @param fileInSpace         新开辟的路径下应该映射的文件
     */
    private void downloadToSpace(String item, FileIndex fileIndexByUniqueNo, File fileInSpace) {
        //缓存不存在则下载至下载空间中
        File fileInTemp = new File(tempPath + item);
        if (!fileInTemp.exists()) {
            fileDownLoad.downloadFile(item, fileIndexByUniqueNo.getFtpPath());
        }
        //拷贝到下载空间
        try {
            FileUtils.copyFile(fileInTemp, fileInSpace);
        } catch (IOException e) {
            log.error("从缓存空间复制到下载空间失败", e);
        }
    }

    private String copyTempDownloadFromTemp(String fileUniqueNo, String distName, File file) {
        String fileTempDownload = null;

        //复制到预览区
        File fileDownloadTemp = new File(tempDownloadPath + fileUniqueNo + File.separator + fileUniqueNo);
        try {
            FileUtils.copyFile(file, fileDownloadTemp);
            fileTempDownload = distName;
            if (fileDownloadTemp.renameTo(new File(fileTempDownload))) {
                log.info("文件复制到临时下载区成功：{}", fileTempDownload);
            }
        } catch (IOException e) {
            log.error("复制文件到临时下载区异常", e);
        }

        return fileTempDownload;
    }

}
