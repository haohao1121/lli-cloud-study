package com.sky.lli.util.restful;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


/**
 * 描述: 统一返回实体类
 *
 * @author klaus
 * @date 2020-01-01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = -8688072985562369039L;
    private String code;
    private String message;
    private T data;
}
