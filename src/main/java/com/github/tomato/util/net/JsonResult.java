package com.github.tomato.util.net;

import com.github.tomato.util.ExDefinition;
import com.github.tomato.util.ExFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

/**
 * @author liuxin
 * 2023/2/22 16:10
 */
public class JsonResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(JsonResult.class);
    public static final JsonResult<Void> OK = success();
    private int code = 0;
    private String msg;
    private T data;

    private JsonResult() {
        this.data = null;
    }


    public void mustSuccess() {
        if (!isSuccess()) {
        }
    }

    public static <T> JsonResult<T> success() {
        return new JsonResult();
    }

    public static <T> JsonResult<T> success(T data) {
        JsonResult<T> result = success();
        result.setData(data);
        return result;
    }


    public boolean isSuccess() {
        return this.code == 0;
    }


    public boolean checkSuccess() {
        return this.isSuccess();
    }


    public int getCode() {
        return this.code;
    }

    public JsonResult<T> setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return this.msg;
    }

    public JsonResult<T> setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return this.data;
    }

    public JsonResult<T> setData(T data) {
        this.data = data;
        return this;
    }

    public int getShortCode() {
        return (this.code <= 1010000000 || this.code >= 2099999999) && (this.code <= 100000000 || this.code >= 209999999) ? this.code : this.code % 10000;
    }

    public String figureAppCode() {
        if (this.code == 0) {
            return null;
        } else {
            String codeString = String.valueOf(this.code);
            return codeString.length() != 9 && codeString.length() != 10 ? null : codeString.substring(2, codeString.length() - 4);
        }
    }

    public long getTimestamp() {
        return System.currentTimeMillis();
    }

}
