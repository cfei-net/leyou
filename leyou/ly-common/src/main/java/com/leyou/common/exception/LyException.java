package com.leyou.common.exception;

import com.leyou.common.enums.ExceptionEnum;
import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class LyException extends RuntimeException{
    private Integer status; //  自定义状态码

    public LyException(Integer status, String message){
        super(message);
        this.status = status;
    }

    public LyException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.status = exceptionEnum.getStatus();
    }
}
