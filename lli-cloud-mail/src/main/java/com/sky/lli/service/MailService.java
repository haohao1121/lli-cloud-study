package com.sky.lli.service;

import com.sky.lli.model.MailModel;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.service.MailService
 * VERSION:   1.0
 * Created by lihao
 * DATE: 2019-05-24
 */

public interface MailService {

    /**
     * Date 2019-05-24
     * Author lihao
     * 方法说明: 发送邮件
     *
     * @param mailModel 邮件信息
     */
    void sendMail(MailModel mailModel);
}
