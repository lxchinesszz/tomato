package com.github.tomato.support;

/**
 * @author liuxin
 * 2020/12/19 8:52 下午
 */
public class User {

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
