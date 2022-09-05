package com.entity.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义异常
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerException extends RuntimeException {

    private Integer code;
    private String msg;

}
