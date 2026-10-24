package com.bq.exception;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {

    private final Integer code;

    public BizException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
