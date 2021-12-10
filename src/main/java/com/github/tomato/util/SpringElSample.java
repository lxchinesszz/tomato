package com.github.tomato.util;

/**
 * 给开发者提供一下常用的EL示例
 *
 * @author liuxin
 * 2021/12/10 7:57 下午
 */
public class SpringElSample {

    /**
     * 提供常用的Spring EL表达公式
     */
    public static void testExample() {
        // 表达式解析器
        User liuxin = new User("liuxin", 23, new Phone("123213321"));

        // 执行toString方法
        System.out.println(ExpressionUtils.getElValue("toString()", liuxin));
        System.out.println(ExpressionUtils.getThisElValue("${name}", liuxin));
        // 支持从根元素获取数据
        System.out.println(ExpressionUtils.getThisElValue("S_AO:${name}", liuxin));
        System.out.println(ExpressionUtils.getThisElValue("${name + '_后缀'}", liuxin));
        // 支持从变量元素获取数据，根元素 = c  #是变量，$是模板占位符
        System.out.println(ExpressionUtils.getThisElValue("${#c.name}, ${#c.age}", liuxin));
        // 获取toString方法
        System.out.println(ExpressionUtils.getThisElValue("${#c.toString()}", liuxin));
        // 获取值并处理
        System.out.println(ExpressionUtils.getThisElValue("${#c.age +'-'+ #c.age}", liuxin));
        // 获取值,并通过方法计算
        System.out.println(ExpressionUtils.getThisElValue("${T(Integer).parseInt(#c.age + 1)}", liuxin));
        // 计算哈希值
        System.out.println(ExpressionUtils.getThisElValue("${T(com.github.tomato.support.DefaultTokenProviderSupportTest).hash(#c.age + 1)}", liuxin));
        System.out.println(ExpressionUtils.getThisElValue("${T(com.github.tomato.support.DefaultTokenProviderSupportTest).json(#c)}", liuxin));

    }

    static class User {

        private String name;

        private Integer age;

        private Phone phone;

        public User(String name, Integer age, Phone phone) {
            this.name = name;
            this.age = age;
            this.phone = phone;
        }

        public Phone getPhone() {
            return phone;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    static class Phone {

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
}
