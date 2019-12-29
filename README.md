![tomato banner](https://img.springlearn.cn/blog/learn_1577637906000.png)

### Tomato

---
![](https://img.shields.io/badge/build-passing-brightgreen.svg) ![](https://img.shields.io/badge/license-Apache%202-blue.svg)
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

是一款专门为`SpringBoot`项目设计的幂等组件


## 原理

### 均匀请求内 

![幂等范围](https://img.springlearn.cn/blog/learn_1577636813000.png)

### 范围控制

每次请求设置当前请求的幂等范围,范围内请求均会被拦截

![范围限制](https://img.springlearn.cn/blog/learn_1577636874000.png)



```java
public interface Idempotent {

    /**
     * @param uniqueCode  唯一码
     * @param millisecond 幂等事件
     * @return Boolean
     */
    boolean idempotent(String uniqueCode, Long millisecond);

    /**
     * @param uniqueCode        唯一码
     * @param millisecond       幂等事件
     * @param exceptionSupplier 异常生成
     * @param <E>               异常泛型
     * @throws E 泛型
     */
    <E extends Throwable> void idempotent(String uniqueCode, Long millisecond, Supplier<? extends E> exceptionSupplier) throws E;
}
```

