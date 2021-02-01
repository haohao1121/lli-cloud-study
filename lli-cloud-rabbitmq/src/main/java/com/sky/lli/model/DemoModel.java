package com.sky.lli.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 描述：
 * CLASSPATH: com.sky.lli.model.DemoModel
 * VERSION:   1.0
 * DATE: 2019-04-24
 * @author lihao
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DemoModel implements Serializable {

    private String id;
    private String name;
    private Integer age;
}
