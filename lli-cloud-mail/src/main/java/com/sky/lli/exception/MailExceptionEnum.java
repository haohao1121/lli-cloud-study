package com.sky.lli.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author lihao
 */

public enum MailExceptionEnum implements IExceptionEnum {

    //邮件相关异常信息
    MAIL_TO_PARAM_MISSING("LLI-15001", "收件人不能为空"),
    MAIL_SUBJECT_PARAM_MISSING("LLI-15002", "邮件主题不能为空"),
    MAIL_BODY_PARAM_MISSING("LLI-15003", "邮件内容不能为空"),
    MAIL_IMG_INLINE_FAIL("LLI-15004", "静态资源添加失败"),
    MAIL_FILE_DOWNLOAD_FAIL("LLI-15005", "文件下载失败"),
    MAIL_FILE_MKDIR_FAIL("LLI-15006", "文件夹创建失败"),

    // region 自身Get方法和构造函数
    ;
    @Getter
    private String code;
    @Getter
    @Setter
    private String message;

    MailExceptionEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
