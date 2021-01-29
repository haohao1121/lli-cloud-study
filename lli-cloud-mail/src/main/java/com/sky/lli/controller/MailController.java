package com.sky.lli.controller;

import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.model.MailModel;
import com.sky.lli.service.MailService;
import com.sky.lli.util.restful.ResponseResult;
import com.sky.lli.util.restful.ResultResponseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.controller.MailController
 * VERSION:   1.0
 * DATE: 2019-05-24
 *
 * @author lihao
 */

@RestController
@RequestMapping("/mail")
public class MailController {
    /**
     * 发送邮件
     */
    private static final String SEND_MAIL = "sendMail";

    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 发送邮件
     *
     * @param mailModel 邮件参数
     */
    @PostMapping(SEND_MAIL)
    public ResponseResult<Object> sendMail(@RequestBody MailModel mailModel) {
        if (null == mailModel) {
            return ResultResponseUtils.error(ExceptionEnum.SYS_REQUEST_PARAM_MISSING);
        }
        //发送邮件
        this.mailService.sendMail(mailModel);

        return ResultResponseUtils.success();
    }
}
