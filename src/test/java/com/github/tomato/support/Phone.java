package com.github.tomato.support;

/**
 * @author liuxin
 * 2020/12/19 9:03 下午
 */
public class Phone {

    private String phoneNo;

    public Phone(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    @Override
    public String toString() {
        return "Phone{" +
                "phoneNo='" + phoneNo + '\'' +
                '}';
    }
}
