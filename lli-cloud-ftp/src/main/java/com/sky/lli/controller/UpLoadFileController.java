package com.sky.lli.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.lli.config.properties.FileStorageProperties;
import com.sky.lli.enums.ExceptionEnum;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.fileindex.IFileIndexService;
import com.sky.lli.service.fileupload.IFileUpload;
import com.sky.lli.util.ApacheCompress;
import com.sky.lli.util.MutipartFileHelper;
import com.sky.lli.util.restful.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sky.lli.util.restful.ResultResponseUtils.error;
import static com.sky.lli.util.restful.ResultResponseUtils.success;
import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * 描述：文件上传Controller
 *
 * @author klaus
 */
@RestController
@RequestMapping("/upload")
@Slf4j
public class UpLoadFileController {
    private static final String FILE_NAME_ENCODE_HEADER = "MultiPartFileNameEncode";
    /**
     * 上传永久存储路径(route)
     */
    private static final String UPLOAD = "upload";
    private static final String UPLOAD_TO_COM = "uploadToCom";
    private static final String UPLOAD_FILES = "uploadFiles";
    /**
     * 上传路径(临时生成的文件下载)(route)
     */
    private static final String UPLOAD_SHORT_TIME_FILE = "uploadShortTimeFile";
    private static final String UPLOAD_SHORT_TIME_FILES = "uploadShortTimeFiles";
    public static final String SHOT_TIME_DOWNLOAD_PATH = "shotTimeDownloadPath";
    /**
     * 以base64形式上传图片
     */
    private static final String UPLOAD_BASE64 = "uploadBase64";
    /**
     * 以base64形式上传图片-批量
     */
    private static final String UPLOAD_BASE64_BATCH = "uploadBase64Batch";

    private final FileStorageProperties fileStorageProperties;
    /**
     * 文件索引信息
     */
    private final IFileIndexService fileIndexService;
    /**
     * 文件上传实现
     */
    private final IFileUpload fileUpload;

    /**
     * 内置处理器
     */
    private final StandardServletMultipartResolver standardServletMultipartResolver;

    @Autowired
    public UpLoadFileController(FileStorageProperties fileStorageProperties, IFileIndexService fileIndexService,
                                IFileUpload fileUpload,
                                StandardServletMultipartResolver standardServletMultipartResolver) {
        this.fileStorageProperties = fileStorageProperties;
        this.fileIndexService = fileIndexService;
        this.fileUpload = fileUpload;
        this.standardServletMultipartResolver = standardServletMultipartResolver;
    }

    /**
     * 描述   :上传永久存储文件
     *
     * @return 返回
     */
    @PostMapping(UPLOAD)
    public ResponseResult<Object> upload(HttpServletRequest request,
                                         @RequestPart(value = "fileInfo", required = false) FileIndex fileInfo) {

        //第一步、处理开始时间
        final long starTime = System.currentTimeMillis();

        //第二部、判断 request 是否有文件上传,即多部分请求
        MultipartHttpServletRequest multipartAndTransform = null;
        if (standardServletMultipartResolver.isMultipart(request)) {
            multipartAndTransform = standardServletMultipartResolver.resolveMultipart(request);
        }

        //第三步、如果不是多部请求则返回不支持的请求响应
        if (multipartAndTransform == null) {
            //未识别的请求
            return error(ExceptionEnum.UNSUPPORTED_MEDIA_TYPE);
        }

        //第四部、检查参数
        ExceptionEnum exceptionEnum = checkUploadParam(multipartAndTransform, fileInfo);
        if (exceptionEnum != null) {
            return error(exceptionEnum);
        }

        //第五步、构建FtpFileIndex
        FileIndex fileIndex = handleFileIndex(multipartAndTransform, fileInfo);
        log.info("构建的文件信息:FileIndex:{}", JSON.toJSON(fileIndex));

        //第六步、处理文件上传，已经检查过文件个数必定为一
        MultipartFile multipartFile = null;
        Optional<Map.Entry<String, MultipartFile>> first = multipartAndTransform.getFileMap().entrySet().stream()
                        .findFirst();
        if (first.isPresent()) {
            multipartFile = first.get().getValue();
        }

        //第七步、文件名称不能带上空格
        if (!fileUpload.fileUpload(multipartFile, fileIndex)) {
            return error(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }

        //第八步、数据库记录此FtpFileIndex
        fileIndexService.insert(fileIndex);

        //第十部、返回文件唯一号
        return success(fileIndex.getFileUniqueId());
    }

    /**
     * 描述: 将文件上传到oss并返回共有下载链接
     *
     * @param request  http请求
     * @param fileInfo 文件信息
     *
     * @return filePath 文件在oss的保存地址
     */
    @PostMapping(UPLOAD_TO_COM)
    public ResponseResult<Object> uploadToCom(HttpServletRequest request,
                                              @RequestPart(value = "fileInfo", required = false) FileIndex fileInfo) {
        //第一步、处理开始时间
        final long starTime = System.currentTimeMillis();

        //第二部、判断 request 是否有文件上传,即多部分请求
        MultipartHttpServletRequest multipartAndTransform = null;
        if (standardServletMultipartResolver.isMultipart(request)) {
            multipartAndTransform = standardServletMultipartResolver.resolveMultipart(request);
        }

        //如果不是多部请求则返回不支持的请求响应
        if (multipartAndTransform == null) {
            //未识别的请求
            return error(ExceptionEnum.UNSUPPORTED_MEDIA_TYPE);
        }

        //检查参数
        ExceptionEnum exceptionEnum = checkUploadParam(multipartAndTransform, fileInfo);
        if (exceptionEnum != null) {
            return error(exceptionEnum);
        }

        //数据初始化
        FileIndex fileIndex = handleFileIndex(multipartAndTransform, fileInfo);
        log.info("构建的文件信息:FileIndex:{}", JSON.toJSON(fileIndex));

        //处理文件上传，保证个数为一
        MultipartFile multipartFile = null;
        Optional<Map.Entry<String, MultipartFile>> first = multipartAndTransform.getFileMap().entrySet().stream()
                        .findFirst();
        if (first.isPresent()) {
            multipartFile = first.get().getValue();
        }

        //将文件上传至oss并包装oss路径
        fileIndex.setFilePath(fileUpload.comFileUpload(multipartFile, fileIndex));

        //将数据保存到数据库方便检索
        fileIndexService.insert(fileIndex);

        return success(fileIndex.getFilePath());
    }

    @PostMapping(UPLOAD_FILES)
    public ResponseResult<Object> uploadFiles(HttpServletRequest request, String fileType,
                                              @RequestParam(required = false) String fileName) throws IOException {
        // 第一步、判断 request 是否有文件上传,即多部分请求
        MultipartHttpServletRequest multipartAndTransform = null;
        if (standardServletMultipartResolver.isMultipart(request)) {
            multipartAndTransform = standardServletMultipartResolver.resolveMultipart(request);
        }

        // 第二步、如果是多部请求则进行处理，否则返回不支持的请求响应
        if (multipartAndTransform == null) {
            return error(ExceptionEnum.UNSUPPORTED_MEDIA_TYPE);
        }

        // 第三步、获取下载的UUID
        String uuid = IdUtil.fastSimpleUUID();
        //如果不传递文件名称则默认为临时唯一号
        if (isBlank(fileName)) {
            fileName = uuid;
        }

        // 第四步、生成本次压缩下载的文件目录
        FileUtils.forceMkdir(new File(fileStorageProperties.getDownloadPath() + uuid));

        // 第五步、待压缩文件列表
        ArrayList<File> needToZipFiles = new ArrayList<>();
        // 第六步、前面已经判断必须存在文件
        boolean flag = isBlank(request.getHeader(FILE_NAME_ENCODE_HEADER));
        //从multipartAndTransform保存到本地并添加到needToZipFiles待压缩列表中
        transferFileFromMultipart(multipartAndTransform, uuid, needToZipFiles, flag);
        String compress = ApacheCompress.compressFilesToZip(needToZipFiles,
                                                            fileStorageProperties.getDownloadPath() + uuid
                                                                            + File.separator + FilenameUtils
                                                                            .getBaseName(fileName));
        if (isNull(compress)) {
            return error(null);
        }
        DateTime now = DateTime.now();
        String dateString = now.toString("yyyy-MM-dd");
        FileIndex fileIndex = FileIndex.builder().effect(1).fileName(fileName).filePath(dateString + File.separator)
                        .fileType(fileType).fileUniqueId(dateString + "@" + IdUtil.fastSimpleUUID())
                        .createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
        this.fileUpload.fileUpload(new File(compress), fileIndex.getFilePath(), fileIndex.getFileUniqueId());
        this.fileIndexService.insert(fileIndex);
        if (isNotBlank(compress)) {
            // 返回文件唯一号
            return success(fileIndex.getFileUniqueId());
        } else {
            return error(ExceptionEnum.TEMP_DOWNLOAD_ZIP_EXCEPTION);
        }
    }

    /**
     * 描述   :上传临时下载文件(目前支持单次临时下载一个文件)
     *
     * @return 返回
     */
    @PostMapping(UPLOAD_SHORT_TIME_FILE)
    public ResponseResult<Object> uploadTempFile(HttpServletRequest request,
                                                 @RequestParam(required = false) String fileName,
                                                 @RequestParam(required = false) Boolean toCompress)
                    throws IOException {

        //第一步、判断 request 是否有文件上传,即多部分请求
        MultipartHttpServletRequest multipartAndTransform = null;
        if (standardServletMultipartResolver.isMultipart(request)) {
            multipartAndTransform = standardServletMultipartResolver.resolveMultipart(request);
        }

        //第二步、如果是多部请求则进行处理，否则返回不支持的请求响应，必须存在上传的文件数量唯一
        if (multipartAndTransform == null || multipartAndTransform.getFileMap().size() < 1) {
            //未识别的请求
            return error(ExceptionEnum.UNSUPPORTED_MEDIA_TYPE);
        }

        //第三步、获取下载的UUID，如果不传递文件名称则默认为临时唯一号
        String uuid = IdUtil.fastSimpleUUID();
        if (!isNotBlank(fileName)) {
            fileName = uuid;
        }

        //第四步、生成本次压缩下载的文件目录
        FileUtils.forceMkdir(new File(fileStorageProperties.getDownloadPath() + uuid));

        //第五步、前面已经判断必须存在文件
        Optional<Map.Entry<String, MultipartFile>> first = multipartAndTransform.getFileMap().entrySet().stream()
                        .findFirst();
        MultipartFile multiPartFile;
        if (first.isPresent()) {
            multiPartFile = first.get().getValue();
        } else {
            return error(ExceptionEnum.UNSUPPORTED_MEDIA_TYPE);
        }

        //第六步、取到文件
        String filePath = fileStorageProperties.getDownloadPath() + uuid + '/' + fileName;
        File file = new File(filePath);
        multiPartFile.transferTo(file.getAbsoluteFile());

        //第八步、如果不需要压缩则直接返回
        if (toCompress == null || !toCompress) {
            return success(filePath.substring(filePath.indexOf(SHOT_TIME_DOWNLOAD_PATH), filePath.length()));
        }

        //第九步、压缩文件返回压缩后的文件路径
        ArrayList<File> needToZipFiles = new ArrayList<>();
        needToZipFiles.add(file);
        String compressFileName = fileStorageProperties.getDownloadPath() + uuid + File.separator + FilenameUtils
                        .getBaseName(fileName);
        String compress = ApacheCompress.compressFilesToZip(needToZipFiles, compressFileName);

        //第十步、判断是否生成成功
        if (isNotBlank(compress)) {
            //返回文件唯一号
            String resultPath = compress.substring(compress.indexOf(SHOT_TIME_DOWNLOAD_PATH), compress.length());
            return success(resultPath);
        } else {
            return error(ExceptionEnum.TEMP_DOWNLOAD_ZIP_EXCEPTION);
        }
    }

    /**
     * 描述   :上传临时下载文件(多个文件)
     *
     * @return 返回
     */
    @PostMapping(UPLOAD_SHORT_TIME_FILES)
    public ResponseResult<Object> uploadTempFiles(HttpServletRequest request,
                                                  @RequestParam(required = false) String fileName) throws IOException {

        //第一步、判断 request 是否有文件上传,即多部分请求
        MultipartHttpServletRequest multipartAndTransform = null;
        if (standardServletMultipartResolver.isMultipart(request)) {
            multipartAndTransform = standardServletMultipartResolver.resolveMultipart(request);
        }

        //第二步、如果是多部请求则进行处理，否则返回不支持的请求响应
        if (multipartAndTransform == null) {
            return error(ExceptionEnum.UNSUPPORTED_MEDIA_TYPE);
        }

        //第三步、获取下载的UUID
        String uuid = IdUtil.fastSimpleUUID();
        //如果不传递文件名称则默认为临时唯一号
        if (isBlank(fileName)) {
            fileName = uuid;
        }

        //第四步、生成本次压缩下载的文件目录
        FileUtils.forceMkdir(new File(fileStorageProperties.getDownloadPath() + uuid));

        //第五步、待压缩文件列表
        ArrayList<File> needToZipFiles = new ArrayList<>();

        //第六步、前面已经判断必须存在文件
        boolean flag = isBlank(request.getHeader(FILE_NAME_ENCODE_HEADER));
        //从multipartAndTransform保存到本地并添加到needToZipFiles待压缩列表中
        transferFileFromMultipart(multipartAndTransform, uuid, needToZipFiles, flag);
        //压缩输出完成路径
        String distCompressName = fileStorageProperties.getDownloadPath() + uuid + File.separator + FilenameUtils
                        .getBaseName(fileName);
        String compress = ApacheCompress.compressFilesToZip(needToZipFiles, distCompressName);
        if (isNotBlank(compress)) {
            //返回文件唯一号
            String resultPath = compress.substring(compress.indexOf(SHOT_TIME_DOWNLOAD_PATH), compress.length());
            return success(resultPath);
        } else {
            return error(ExceptionEnum.TEMP_DOWNLOAD_ZIP_EXCEPTION);
        }
    }

    @PostMapping(UPLOAD_BASE64)
    public ResponseResult<Object> uploadBase64(@RequestBody String base64,
                                               @RequestPart(value = "fileInfo", required = false) FileIndex fileInfo) {

        //第一步、处理开始时间
        final long starTime = System.currentTimeMillis();
        //将base64转化为MultipartFile
        MultipartFile multipartFile = MutipartFileHelper.base64ToMultipart(base64);
        //构建FtpFileIndex
        FileIndex fileIndex = handleFileIndexWithBase64(fileInfo);
        log.info("构建的文件信息:FileIndex:{}", JSON.toJSON(fileIndex));

        //文件名称不能带上空格
        if (!fileUpload.fileUpload(multipartFile, fileIndex)) {
            return error(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }

        //数据库记录此FtpFileIndex
        fileIndexService.insert(fileIndex);

        //返回文件唯一号
        return success(fileIndex.getFileUniqueId());
    }

    @PostMapping(UPLOAD_BASE64_BATCH)
    public ResponseResult<Object> uploadBase64Batch(@RequestBody List<JSONObject> batchFiles) {
        ArrayList<JSONObject> jsonObjects = new ArrayList<>();
        batchFiles.forEach(item -> {
            ResponseResult<Object> objectResponseResult = uploadBase64(item.getString("fileCode"), FileIndex.builder()
                            .fileName(item.getString("fileName")).build());
            if (!isNull(objectResponseResult)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("fileName", item.getString("fileName"));
                jsonObject.put("fileNo", objectResponseResult.getData());
                jsonObjects.add(jsonObject);
            }
        });
        //返回文件唯一号
        return success(jsonObjects);
    }

    /**
     * MultipartHttpServletRequest中的文件保存到本地
     *
     * @param multipartAndTransform 请求对象
     * @param uuid                  命令空间
     * @param needToZipFiles        待压缩文件集合
     * @param flag                  文件名称是否有特殊编码
     */
    private void transferFileFromMultipart(MultipartHttpServletRequest multipartAndTransform, String uuid,
                                           ArrayList<File> needToZipFiles, boolean flag) {
        multipartAndTransform.getMultiFileMap().forEach((key, value) -> {
            if (CollectionUtils.isEmpty(value)) {
                return;
            }
            List<File> collect = value.stream().map(multipartFile -> {
                // 取到文件
                String filePath = fileStorageProperties.getDownloadPath() + uuid + File.separator;
                if (flag) {
                    filePath += multipartFile.getOriginalFilename();
                } else {
                    filePath += new String(Base64.getDecoder().decode(multipartFile.getOriginalFilename()));
                }
                File file = new File(filePath);
                try {
                    multipartFile.transferTo(file.getAbsoluteFile());
                } catch (IOException e) {
                    log.error("UPLOAD_SHORT_TIME_FILES,处理上传文件异常", e);
                }
                return file;
            }).collect(Collectors.toList());
            needToZipFiles.addAll(collect);
        });
    }

    //============================================私有方法============================================

    /**
     * 校验传入文件服务的参数是否合法，以及构建完整的文件索引信息
     *
     * @param multipartHttpServletRequest 多部处理
     * @param fileIndex                   文件信息对象
     */
    private ExceptionEnum checkUploadParam(MultipartHttpServletRequest multipartHttpServletRequest,
                                           FileIndex fileIndex) {
        //文件不能为空
        if (multipartHttpServletRequest.getFileMap().isEmpty()) {
            return ExceptionEnum.UPLOAD_PARAM_FILE_INPUT_STREAM_IS_NULL;
        }

        //文件只能存在一个
        if (multipartHttpServletRequest.getFileMap().size() > 1) {
            return ExceptionEnum.UPLOAD_PARAM_FILE_INPUT_STREAM_TOO_MUCH;
        }

        //文件信息不为空(模块传递数据)则进行完整校验
        // 否则(前端传递文件)进行只校验是否存在文件
        if (fileIndex == null) {
            return null;
        }

        //模块名为空
        if (!isNotBlank(fileIndex.getFileType())) {
            return ExceptionEnum.UPLOAD_PARAM_FILE_UPLOADER_ENUM_IS_NULL;
        }

        return null;
    }

    /**
     * 处理上传参数建立本域的{@link FileIndex}
     *
     * @param multipartHttpServletRequest 多部处理
     *
     * @return 返回处理结果
     */
    private FileIndex handleFileIndex(MultipartHttpServletRequest multipartHttpServletRequest, FileIndex fileIndex) {
        //生成文件路径
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String fileDescribe;
        String fileName = "";
        String fileType = "";
        Map<String, String> paramMap;
        if (fileIndex != null) {
            fileName = fileIndex.getFileName();
            fileDescribe = fileIndex.getFileDesc();
            fileType = fileIndex.getFileType();
            paramMap = fileIndex.getAttachMap();
        } else {
            fileDescribe = StringUtils.EMPTY;
            paramMap = null;
        }
        //如果传递了文件名则必须使用传递的文件名
        if (fileIndex != null && isNotBlank(fileIndex.getFileName())) {
            fileName = fileIndex.getFileName();
        } else {
            //参数校验中只存在一个
            fileName = generateFileNameByRequest(multipartHttpServletRequest, fileName);
        }

        //替换文件中的中文名称
        fileName = fileName.replaceAll(" ", StringUtils.EMPTY);
        //构建索引
        Date dateNow = new Date();
        String dateString = df.format(dateNow);
        return FileIndex.builder()
                        // 设置有效
                        .effect(1)
                        // 附加参数
                        .fileDesc(fileDescribe)
                        // 文件名称
                        .fileName(fileName)
                        // 生成文件ID
                        .fileUniqueId(dateString + "@" + IdUtil.fastSimpleUUID())
                        // 生成路径
                        .filePath(dateString + "/")
                        // 文件上传模块
                        .fileType(fileType)
                        // 文件用途
                        .attachMap(paramMap).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
    }

    /**
     * base64上传只存在内容
     * 处理上传参数建立本域的{@link FileIndex}
     *
     * @return 返回处理结果
     */
    private FileIndex handleFileIndexWithBase64(FileIndex fileIndex) {
        //生成文件路径
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String fileDescribe;
        String fileName;
        String fileType = "";
        String fileUUID = IdUtil.fastSimpleUUID();
        Map<String, String> paramMap;
        if (fileIndex != null) {
            fileName = fileIndex.getFileName();
            fileDescribe = fileIndex.getFileDesc();
            fileType = fileIndex.getFileType();
            paramMap = fileIndex.getAttachMap();
        } else {
            fileDescribe = StringUtils.EMPTY;
            paramMap = null;
            fileName = fileUUID + ".png";
        }

        //替换文件中的中文名称
        fileName = fileName.replaceAll(" ", StringUtils.EMPTY);
        //构建索引
        Date dateNow = new Date();
        String dateString = df.format(dateNow);
        return FileIndex.builder()
                        // 设置有效
                        .effect(1)
                        // 附加参数
                        .fileDesc(fileDescribe)
                        // 文件名称
                        .fileName(fileName)
                        // 生成文件ID
                        .fileUniqueId(dateString + "@" + fileUUID)
                        // 生成路径
                        .filePath(dateString + "/")
                        // 文件上传模块
                        .fileType(fileType)
                        // 文件用途
                        .attachMap(paramMap).createTime(LocalDateTime.now()).updateTime(LocalDateTime.now()).build();
    }

    private String generateFileNameByRequest(MultipartHttpServletRequest multipartHttpServletRequest, String fileName) {
        for (Map.Entry<String, MultipartFile> file : multipartHttpServletRequest.getFileMap().entrySet()) {
            String originalFilename = file.getValue().getOriginalFilename();
            if (isNotBlank(multipartHttpServletRequest.getHeader(FILE_NAME_ENCODE_HEADER))) {
                fileName = new String(Base64.getDecoder().decode(originalFilename));
            } else {
                fileName = originalFilename;
            }
        }
        return fileName;
    }
}
