package com.leyou.common.advice;

import com.leyou.common.exception.ExceptionResult;
import com.leyou.common.exception.LyException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice // 控制器通知：增强，使用aop思想统一处理异常
public class BaseExceptionHandler {

    @ExceptionHandler(LyException.class) // 处理那些异常
    public ResponseEntity<ExceptionResult> exceptionHandler(LyException e){
        return ResponseEntity
                .status(e.getStatus()) //获取的状态码
                .body(new ExceptionResult(e));
    }
}
