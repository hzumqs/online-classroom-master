package com.entity.exception;

import com.entity.result.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 统一异常处理类
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    //全局异常处理
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result error(Exception e){
        e.printStackTrace();
        return Result.error().message("执行了全局异常处理");
    }
    //特定异常处理
    @ExceptionHandler({ArithmeticException.class,NullPointerException.class})
    @ResponseBody
    public Result error(ArithmeticException e){
        e.printStackTrace();
        return Result.error().message("执行了特定异常处理");
    }

    //自定义异常处理
    @ExceptionHandler(CustomerException.class)
    @ResponseBody
    public Result error(CustomerException e){
        e.printStackTrace();
        return Result.error().code(e.getCode()).message(e.getMsg());
    }
}
