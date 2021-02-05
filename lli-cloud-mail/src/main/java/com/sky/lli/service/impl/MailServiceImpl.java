package com.sky.lli.service.impl;

import cn.hutool.core.map.MapUtil;
import com.sky.lli.exception.ExceptionEnum;
import com.sky.lli.exception.MailExceptionEnum;
import com.sky.lli.exception.ServiceException;
import com.sky.lli.model.MailModel;
import com.sky.lli.model.TemplateParam;
import com.sky.lli.model.mongo.MailMongoFile;
import com.sky.lli.service.MailService;
import com.sky.lli.util.FileDownloadUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import javax.mail.internet.MimeUtility;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 描述：
 * VERSION:   1.0
 * DATE: 2019-05-24
 *
 * @author lihao
 */

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class MailServiceImpl implements MailService {

    /**
     * 临时文件下载目录
     */
    @Value("${file.download.path}")
    private String fileSavePath;
    @Resource
    private JavaMailSenderImpl mailSender;
    @Resource
    private TemplateEngine templateEngine;
    @Resource
    private MongoTemplate mongoTemplate;

    /**
     * Date 2019-05-24
     * Author lihao
     * 方法说明: 发送邮件
     *
     * @param mailModel 邮件信息
     */
    @Override
    public void sendMail(MailModel mailModel) {
        try {
            //校验参数
            checkMail(mailModel);
            //发送邮件
            sendMimeMail(mailModel);
            //保存邮件
            saveMailModel(mailModel);
        } catch (Exception e) {
            log.error("邮件发送失败:错误信息:{}", e.getMessage());
        }
    }

    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 构建复杂邮件信息类
     *
     * @param mailVo 邮件参数
     */
    private void sendMimeMail(MailModel mailVo) {
        try {
            //构建复杂邮件信息,true表示支持复杂类型
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), Boolean.TRUE);
            //邮件发信人从配置项读取
            mailVo.setFrom(getMailSendFrom(mailVo));
            //邮件发信人
            messageHelper.setFrom(mailVo.getFrom());
            //邮件收信人
            messageHelper.setTo(mailVo.getTo().split(","));
            //邮件主题
            messageHelper.setSubject(mailVo.getSubject());
            //邮件内容
            messageHelper.setText(mailVo.getText());
            //发送时间
            messageHelper.setSentDate(new Date());
            //抄送
            if (!StringUtils.isEmpty(mailVo.getCc())) {
                messageHelper.setCc(mailVo.getCc().split(","));
            }
            //密送
            if (!StringUtils.isEmpty(mailVo.getBcc())) {
                messageHelper.setCc(mailVo.getBcc().split(","));
            }
            //添加静态资源
            if (!CollectionUtils.isEmpty(mailVo.getMailInLineMap())) {
                messageHelper.setText(mailVo.getText(), Boolean.TRUE);
                buildAddInLine(mailVo, messageHelper);
            }
            //添加邮件附件
            if (mailVo.getMultipartFiles() != null) {
                for (MultipartFile multipartFile : mailVo.getMultipartFiles()) {
                    //MimeUtility.encodeWord 防止中文乱码
                    messageHelper.addAttachment(MimeUtility.encodeWord(Objects.requireNonNull(multipartFile.getOriginalFilename())), multipartFile);
                }
            }
            //文件附件url集合
            if (MapUtil.isNotEmpty(mailVo.getAttachmentMap())) {
                for (Map.Entry<String, String> entry : mailVo.getAttachmentMap().entrySet()) {
                    //先将文件下载到本地,然后添加到邮件里
                    String filePath = FileDownloadUtil.downloadFile(entry.getValue(), fileSavePath);
                    FileSystemResource res = new FileSystemResource(new File(filePath));
                    messageHelper.addAttachment(MimeUtility.encodeWord(Objects.requireNonNull(res.getFilename())), res);
                }
            }
            //模板邮件
            if (!StringUtils.isEmpty(mailVo.getTemplateName()) && !CollectionUtils.isEmpty(mailVo.getTemplateParams())) {
                Context context = getContext(mailVo);
                String emailContext = templateEngine.process(mailVo.getTemplateName(), context);
                messageHelper.setText(emailContext, Boolean.TRUE);
            }

            //正式发送邮件
            mailSender.send(messageHelper.getMimeMessage());
            log.info("发送邮件成功：{}->{}", mailVo.getFrom(), mailVo.getTo());
        } catch (Exception e) {
            log.error("邮件发送失败,错误信息如下:{}", e.getMessage());
            throw new ServiceException(ExceptionEnum.SYS_INVOKING_ERROR);
        }
    }


    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 添加静态资源信息
     *
     * @param mailModel 邮件信息
     */
    private void buildAddInLine(MailModel mailModel, MimeMessageHelper messageHelper) {
        try {
            for (Map.Entry<String, String> entry : mailModel.getMailInLineMap().entrySet()) {
                //如果是url文件,先将文件下载到本地,然后添加到邮件里
                String filePath = FileDownloadUtil.downloadFile(entry.getValue(), fileSavePath);
                FileSystemResource res = new FileSystemResource(new File(filePath));
                messageHelper.addInline(MimeUtility.encodeWord(Objects.requireNonNull(res.getFilename())), res);
            }
        } catch (Exception e) {
            log.error("添加静态资源失败,错误信息如下:{}", e.getMessage());
            throw new ServiceException(MailExceptionEnum.MAIL_IMG_INLINE_FAIL);
        }
    }

    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 封装模板引擎参数
     *
     * @param mailModel 邮件参数
     */
    private Context getContext(MailModel mailModel) {
        Context context = new Context();
        for (TemplateParam tm : mailModel.getTemplateParams()) {
            context.setVariable(tm.getPropertyName(), tm.getPropertyValue());
            if (tm.getPropertyType() == 1) {
                String filePath = FileDownloadUtil.downloadFile(tm.getPropertyValue(), fileSavePath);
                context.setVariable(tm.getPropertyName(), filePath);
            }

        }
        return context;
    }

    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 数据库保存邮件信息
     *
     * @param mailModel 邮件信息
     */
    private void saveMailModel(MailModel mailModel) {
        //数据库保存邮件记录
        MailMongoFile mailMongo = new MailMongoFile();
        mailMongo.setDataInfo(mailModel);

        //保存到MongoDB
        this.mongoTemplate.save(mailMongo);
    }

    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 校验参数
     *
     * @param mailModel 邮件参数
     */
    private void checkMail(MailModel mailModel) {
        if (StringUtils.isEmpty(mailModel.getTo())) {
            throw new ServiceException(MailExceptionEnum.MAIL_TO_PARAM_MISSING);
        }
        if (StringUtils.isEmpty(mailModel.getSubject())) {
            throw new ServiceException(MailExceptionEnum.MAIL_SUBJECT_PARAM_MISSING);
        }
        if (StringUtils.isEmpty(mailModel.getText())) {
            throw new ServiceException(MailExceptionEnum.MAIL_BODY_PARAM_MISSING);
        }
    }

    /**
     * Date 2019-05-25
     * Author lihao
     * 方法说明: 获取邮件发信人
     *
     * @param mailModel 邮件参数
     * @return 收件人
     */
    public String getMailSendFrom(MailModel mailModel) {
        if (!StringUtils.isEmpty(mailModel.getFrom())) {
            return mailModel.getFrom();
        }
        return mailSender.getJavaMailProperties().getProperty("from");
    }
}
