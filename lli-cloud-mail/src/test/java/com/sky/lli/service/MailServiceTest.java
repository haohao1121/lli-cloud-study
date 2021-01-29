package com.sky.lli.service;

import cn.hutool.core.util.IdUtil;
import com.sky.lli.model.MailModel;
import com.sky.lli.model.TemplateParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.service.MailServiceTest
 * VERSION:   1.0
 * Created by lihao
 * DATE: 2019-05-25
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class MailServiceTest {

    @Resource
    private MailService mailService;


    @Test
    public void testSimpleMail() {
        this.mailService.sendMail(buildMailModel());
    }


    @Test
    public void testInlineResourceMail() {
        String rscId = "neo006";
        String content = "<html><body>这是有图片的邮件：<img src=\'cid:" + rscId + "\' > </br></body></html>";
        // String imgPath = "/Users/lihao/Desktop/图片/BG-1.jpg";
        String imgPath = "https://www.baidu.com/img/flexible/logo/pc/index@2.png";
        MailModel model = buildMailModel();
        model.setText(content);
        Map<String, String> inlineMap = new HashMap<>();
        inlineMap.put(rscId, imgPath);
        model.setMailInLineMap(inlineMap);

        model.setAttachmentMap(buildAttachmentFile());

        this.mailService.sendMail(model);
    }

    @Test
    public void testTemplateMail() {
        MailModel mailModel = buildMailModel();
        mailModel.setTemplateName("demoEmailTemplate");
        Map<String, Object> map = new HashMap<>();
        map.put("userName", "lihao");
        map.put("home", "天下山庄");
        map.put("httpImg", "https://www.baidu.com/img/flexible/logo/pc/index@2.png");
        map.put("localImg", "/Users/lihao/Desktop/图片/BG-1.jpg");

        mailModel.setTemplateParams(buildTemplateParam());

        mailModel.setAttachmentMap(buildAttachmentFile());

        this.mailService.sendMail(mailModel);
    }

    private Map<String, String> buildAttachmentFile() {
        Map<String, String> map = new HashMap<>();
        map.put("httpImg", "https://www.baidu.com/img/flexible/logo/pc/index@2.png");
        map.put("localImg", "/Users/lihao/Desktop/图片/BG-1.jpg");
        return map;
    }

    //模板邮件参数
    private List<TemplateParam> buildTemplateParam() {
        //参数
        List<String> params = Arrays.asList("userName", "home", "httpImg", "localImg");

        List<TemplateParam> list = new ArrayList<>();
        for (String p : params) {
            TemplateParam param = new TemplateParam();
            param.setPropertyName(p);
            param.setPropertyValue(p + "_value");
            if (p.equals("httpImg")) {
                param.setPropertyType(1);
                param.setPropertyValue("http://vm-1.lli.com:8099/images/guide/bj-floor-8-1.jpg");
            }
            if (p.equals("localImg")) {
                param.setPropertyType(1);
                param.setPropertyValue("/Users/lihao/Desktop/图片/BG-1.jpg");
            }

            list.add(param);
        }

        return list;
    }


    //构建邮件主题信息
    private MailModel buildMailModel() {
        MailModel mailModel = new MailModel();
        mailModel.setId(IdUtil.simpleUUID());
        mailModel.setTo("1064231507@qq.com");
        mailModel.setSubject("testMail");
        mailModel.setText("this is test mail!");
        mailModel.setCc("812080908@qq.com");

        return mailModel;
    }
}
