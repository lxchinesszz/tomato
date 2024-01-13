package com.github.tomato.util;

import org.slf4j.helpers.MessageFormatter;

import java.io.Serializable;

/**
 * @author liuxin
 * 2023/2/22 16:03
 */
public class ExDefinition implements Serializable {

    private static final long serialVersionUID = 1L;
    private ExType exType;
    private int code;
    private String desc;
    private String copyright;

    public ExDefinition() {
    }

    public ExDefinition(ExType exType, int code, String desc) {
        this.exType = exType;
        this.code = code;
        this.desc = desc;
    }

    public ExDefinition(ExType exType, int code, String descFormat, Object... messages) {
        this.exType = exType;
        this.code = code;
        if (messages != null && messages.length > 0) {
            this.desc = MessageFormatter.arrayFormat(descFormat, messages).getMessage();
        } else {
            this.desc = descFormat;
        }

    }

    public ExDefinition(ExType exType, int code, String desc, String copyright) {
        this.exType = exType;
        this.code = code;
        this.desc = desc;
        this.copyright = copyright;
    }


    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return this.desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public ExType getExType() {
        return this.exType;
    }

    public void setExType(ExType exType) {
        this.exType = exType;
    }

    public int getShortCode() {
        return (this.code <= 1010000000 || this.code >= 2099999999) && (this.code <= 100000000 || this.code >= 209999999) ? this.code : this.code % 10000;
    }
}
