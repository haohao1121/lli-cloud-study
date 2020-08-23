package com.sky.lli.controller;

import com.luhuiguo.fastdfs.service.FastFileStorageClient;
import com.sky.lli.model.FileIndex;
import com.sky.lli.service.FileIndexService;
import com.sky.lli.util.json.JsonUtils;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 说明:
 *
 * @author klaus
 * @date 2020/8/22
 */

@Slf4j
@RestController
@RequestMapping("/fileIndex")
public class FileIndexController {

    /**
     * 根据文件唯一号获取文件信息
     */
    private static final String FIND_BY_ID = "findById";
    /**
     * 根据文件唯一号删除文件
     */
    private static final String DELETE_BY_ID = "deleteById";
    /**
     * 查询文件集合
     */
    private static final String FIND_LIST = "findList";
    /**
     * 获取文件分页
     */
    private static final String FIND_PAGE = "findPage";


    @Resource
    private FastFileStorageClient fastFileStorageClient;

    @Resource
    private FileIndexService fileIndexService;


    /**
     * 描述: 根据文件唯一号获取文件信息
     *
     * @param uniqNo 文件唯一ID
     * @date 2020/8/23
     * @author klaus
     */
    @GetMapping(FIND_BY_ID)
    public ResponseResult<Object> findById(@RequestParam("uniqNo") String uniqNo) {
        log.info(" find fileIndex by id :{}", uniqNo);
        return ResultResponseUtils.success(this.fileIndexService.findFileByUniqNo(uniqNo));
    }

    /**
     * 描述: 根据文件唯一号删除文件
     *
     * @param uniqNo 文件唯一ID
     * @date 2020/8/23
     * @author klaus
     */
    @GetMapping(DELETE_BY_ID)
    public ResponseResult<Object> deleteById(@RequestParam("uniqNo") String uniqNo) {
        log.info(" delete file by uniqId, param:{}", uniqNo);

        //先获取文件信息
        FileIndex fileIndex = this.fileIndexService.findFileByUniqNo(uniqNo);
        if (null != fileIndex) {
            log.info(" delete file by path :{}", fileIndex.getFullPath());
            this.fastFileStorageClient.deleteFile(fileIndex.getFullPath());

            log.info(" delete mongodb fileIndex by uniqNo :{}", uniqNo);
            this.fileIndexService.deleteById(uniqNo);
        }
        return ResultResponseUtils.success();
    }


    /**
     * 描述: 获取文件集合
     *
     * @param fileIndex 参数
     * @date 2020/8/23
     * @author klaus
     */
    @PostMapping(FIND_LIST)
    public ResponseResult<Object> findList(@RequestBody FileIndex fileIndex) {
        log.info(" find fileIndex list,  param:{}", JsonUtils.toJson(fileIndex));
        return ResultResponseUtils.success(this.fileIndexService.findFileList(fileIndex));
    }


    /**
     * 描述: 分页查询
     *
     * @param pageNo    当前页
     * @param pageSize  每页条数
     * @param fileIndex 查询条件
     * @date 2020/8/23
     * @author klaus
     */
    @PostMapping(FIND_PAGE)
    public ResponseResult<Object> findPage(@RequestParam(value = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize
            , @RequestBody FileIndex fileIndex) {
        log.info(" find fileIndex page,  pageNo:{}, pageSize:{} ,param:{}", pageNo, pageSize, JsonUtils.toJson(fileIndex));
        return ResultResponseUtils.success(this.fileIndexService.findFilePage(pageNo, pageSize, fileIndex));
    }

}
