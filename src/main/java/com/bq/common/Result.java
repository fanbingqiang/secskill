package com.bq.common;

import com.bq.entity.Goods;
import com.bq.entity.Order;
import lombok.Data;

import java.util.List;

@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;

    public static <T> Result<T> success() {
        return build(200, "成功", null);
    }

    public static <T> Result<T> success(T data) {
        return build(200, "成功", data);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return build(code, msg, null);
    }

    private static <T> Result<T> build(Integer code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }


}