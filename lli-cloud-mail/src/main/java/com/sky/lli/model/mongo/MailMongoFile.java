package com.sky.lli.model.mongo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.model.mongo.MailMongoFile
 * VERSION:   1.0
 * DATE: 2017/12/23
 *
 * @author lihao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "MAIL_HISTORY")
public class MailMongoFile implements Serializable {

    @Id
    private String mongoId;
    private Object dataInfo;
}
