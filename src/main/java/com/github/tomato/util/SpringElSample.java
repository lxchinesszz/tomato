package com.github.tomato.util;

import lombok.Data;

/**
 * 给开发者提供一下常用的EL示例
 *
 * @author liuxin
 * 2021/12/10 7:57 下午
 */
public class SpringElSample {

    @Data
    private static class User {

        private String name;

        private Integer age;

        private final Phone phone;

        public User(String name, Integer age, Phone phone) {
            this.name = name;
            this.age = age;
            this.phone = phone;
        }
    }

    static class Phone {

        private final String phoneNo;

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

    /**
     * 提供常用的Spring EL表达公式
     */
    public static void springElExample() {
        // 表达式解析器
        User jay = new User("liuxin", 23, new Phone("123213321"));

        // 执行toString方法
        System.out.println(ExpressionUtils.getElValue("toString()", jay));
        System.out.println(ExpressionUtils.getThisElValue("${name}", jay));
        // 支持从根元素获取数据
        System.out.println(ExpressionUtils.getThisElValue("S_AO:${name}", jay));
        System.out.println(ExpressionUtils.getThisElValue("${name + '_后缀'}", jay));
        // 支持从变量元素获取数据，根元素 = c  #是变量，$是模板占位符
        System.out.println(ExpressionUtils.getThisElValue("${#c.name}, ${#c.age}", jay));
        // 获取toString方法
        System.out.println(ExpressionUtils.getThisElValue("${#c.toString()}", jay));
        // 获取值并处理
        System.out.println(ExpressionUtils.getThisElValue("${#c.age +'-'+ #c.age}", jay));
        // 获取值,并通过方法计算
        System.out.println(ExpressionUtils.getThisElValue("${T(Integer).parseInt(#c.age + 1)}", jay));
        // 计算哈希值
        System.out.println(ExpressionUtils.getThisElValue("${T(com.github.tomato.support.DefaultTokenProviderSupportTest).hash(#c.age + 1)}", jay));
        System.out.println(ExpressionUtils.getThisElValue("${T(com.github.tomato.support.DefaultTokenProviderSupportTest).json(#c)}", jay));

    }

}
