![](https://img.springlearn.cn/blog/learn_1577637906000.png)


### Tomato

---
![](https://img.shields.io/badge/build-passing-brightgreen.svg) ![](https://img.shields.io/badge/license-Apache%202-blue.svg)
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

是一款专门为`SpringBoot`项目设计的幂等组件



**本文以下的讨论,都是假设我们数据库没有做唯一约束和乐观锁的场景下的分析。关于防重和幂等判断的讨论,欢迎留言讨论**

---

### 一、问题重现
下面这段逻辑,在正常情况下是没有问题的,①也算进行了幂等校验,先判断状态在进行处理。但是当用户重复提交导致并发问题,两次请求都执行到了④步骤,而因为④并没有用乐观锁处理,就会导致幂等性问题。两次提交都对数据状态进行的修改。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020010111420128.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

所以我们得出结论凡是通过根据查询状态来做防重或者幂等校验的，理论情况下都会因为并发问题而被击穿。 所以如果数据库表设计允许的情况下,建议设置唯一约束,用来做幂等或者防重。(sql尽量加上乐观锁操作)

### 二、如何防止重复提交?
#### 方案 1: 幂等表

利用数据库唯一索引做防重处理,当第一次插入是没有问题的,第二次在进行插入会因为唯一索引报错。从而达到拦截的目的。

#### 方案 2: token令牌

如何防止重复提交, 为每次请求生成请求唯一键,服务端对每个唯一键进行生命周期管控。规定时间内只允许一次请求,非第一次请求都属于重复提交。但是前后端改造大,后端要给出单独生成token令牌接口,前端要在每次调用时候先获取token令牌。

#### 方案 3: 基于方法粒度指定唯一键【Tomato】

基于Web方法,从参数中寻找可以作为唯一键,进行控制。改造难度低,仅需要服务端改造,前端无感知。



本文是基于方案 3提供的解决方案,具体实现是利用 Redis进行实现。

### 三、具体实现原理
原理非常简单,我们为每次请求声明一个防重的时间范围,范围内的重复请求都会当做重复提交被拦截。核心原理就是这么简单。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200101114303637.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

基于控制时间两种防重策略

#### 策略一: 滑动窗口策略

每次请求设置当前请求的控制时间,控制时间内请求均会被拦截。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200101114341400.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

#### **策略二: 固定窗口策略**

仅仅为第一次请求生成一个控制时间,控制时间内相同的请求会被拦截,控制时间过期后,以此类推。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020010111440842.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

### 四、代码演示

#### 1.利用拦截器实现

```java
//防重范围时间1000
@Repeat(scope = 1000)
public VoidResponse addWhite0(@TomatoToken String name) {
        return VoidResponse.SUCCESS();
}

//如果是基本类型就直接用,如果是对象模式就从对象里面去取。
@Repeat(scope = 1000)
public VoidResponse addWhite1(@TomatoToken("name") TestDataRequest name) {
        return VoidResponse.SUCCESS();
}

//从表单中获取
@Repeat(scope = 1000)
public VoidResponse addWhite2(@TomatoTokenFrom("name")HttpServletRequest request) {
        return VoidResponse.SUCCESS();
}
```

#### 2.硬编码实现

```java
public VoidResponse addWhite(String name) {
  			Idempotent.idempotent(name,1000,()->new RuntimeException("重复提交"))
        return VoidResponse.SUCCESS();
}
```

