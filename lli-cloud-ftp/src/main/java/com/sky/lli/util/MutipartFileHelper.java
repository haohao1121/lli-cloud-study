package com.sky.lli.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

/**
 * @author lihao
 */
@Slf4j
public class MutipartFileHelper {

    public static MultipartFile base64ToMultipart(String base64) {
        String baseOrg;
        String[] baseStrs = base64.split(",");
        if (baseStrs.length > 1) {
            baseOrg = baseStrs[1];
        } else {
            baseOrg = baseStrs[0];
        }
        return new BASE64DecodedMultipartFile(Base64.getDecoder().decode(baseOrg), baseStrs[0]);
    }

    private MutipartFileHelper() {
    }
}
