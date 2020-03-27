package com.leyou.common.exception;

import lombok.Getter;
import org.joda.time.DateTime;


@Getter
public class ExceptionResult {
    private int status;       // 响应的状态码
    private String message;   // 响应的内容
    private String timestamp; // 响应的时间戳

    public ExceptionResult(LyException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
        this.timestamp = DateTime.now().toString("yyyy-MM-dd HH:mm:ss"); // 使用一个时间的工具包： joda-time
    }
}
