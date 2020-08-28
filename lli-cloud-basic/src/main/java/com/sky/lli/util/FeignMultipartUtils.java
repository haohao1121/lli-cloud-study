package com.sky.lli.util;

import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.exception.UtilsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author lihao (15215401693@163.com)
 * @date 2020/08/28
 */

@Slf4j
public class FeignMultipartUtils {

    /**
     * 方法说明: 将文件转化问 MultipartFile
     *
     * @param file 文件
     *
     * @date 2020-08-28
     * @author lihao
     */

    private MultipartFile getMultipartFile(File file) {
        final DiskFileItem item = new DiskFileItem("file", MediaType.MULTIPART_FORM_DATA_VALUE, true, file.getName(),
                                                   100000000, file.getParentFile());
        try {
            OutputStream os = item.getOutputStream();
            os.write(FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            log.error("test fail-", e);
            throw new UtilsException(ExceptionEnum.SYS_FAILURE_EXCEPTION);
        }
        return new CommonsMultipartFile(item);
    }
}
