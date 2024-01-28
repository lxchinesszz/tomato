
![](https://imgconvert.csdnimg.cn/aHR0cHM6Ly9pbWcuc3ByaW5nbGVhcm4uY24vYmxvZy9sZWFybl8xNTc3NjM3OTA2MDAwLnBuZw?x-oss-process=image/format,png)


### <span id="head1"> [Tomato Website](https://tomato.springlearn.cn)</span>

---
![](https://img.shields.io/badge/build-passing-brightgreen.svg) ![](https://img.shields.io/badge/license-Apache%202-blue.svg)
[![996.icu](https://img.shields.io/badge/link-996.icu-red.svg)](https://996.icu)[![LICENSE](https://img.shields.io/badge/license-Anti%20996-blue.svg)](https://github.com/996icu/996.ICU/blob/master/LICENSE)

是一款专门为`SpringBoot`项目设计的幂等组件


- [ Tomato](#head1)
- [ 一、问题重现](#head2)
- [ 二、如何防止重复提交?](#head3)
	- [方案 1: 幂等表](#head4)
	- [方案 2: token令牌](#head5)
	- [方案 3: 基于方法粒度指定唯一键【Tomato】](#head6)
- [ 三、具体实现原理](#head7)
	- [策略一: 滑动窗口策略](#head8)
	- [**策略二: 固定窗口策略**](#head9)
- [ 四、代码演示](#head10)
	- [ 1.利用拦截器实现](#head11)
	- [ 2.硬编码实现](#head12)
- [ 五、快速使用](#head13)
	- [1. 加入依赖](#head14)
	- [2. 启用成功](#head15)
- [ 六、版本发布记录](#head16)

**本文以下的讨论,都是假设我们数据库没有做唯一约束和乐观锁的场景下的分析。关于防重和幂等判断的讨论,欢迎留言讨论**

---

### <span id="head2"> 一、问题重现</span>
下面这段逻辑,在正常情况下是没有问题的,①也算进行了幂等校验,先判断状态在进行处理。但是当用户重复提交导致并发问题,两次请求都执行到了④步骤,而因为④并没有用乐观锁处理,就会导致幂等性问题。两次提交都对数据状态进行的修改。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020010111420128.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

所以我们得出结论凡是通过根据查询状态来做防重或者幂等校验的，理论情况下都会因为并发问题而被击穿。 所以如果数据库表设计允许的情况下,建议设置唯一约束,用来做幂等或者防重。(sql尽量加上乐观锁操作)

### <span id="head3"> 二、如何防止重复提交?</span>
#### <span id="head4">方案 1: 幂等表</span>

利用数据库唯一索引做防重处理,当第一次插入是没有问题的,第二次在进行插入会因为唯一索引报错。从而达到拦截的目的。

#### <span id="head5">方案 2: token令牌</span>

如何防止重复提交, 为每次请求生成请求唯一键,服务端对每个唯一键进行生命周期管控。规定时间内只允许一次请求,非第一次请求都属于重复提交。但是前后端改造大,后端要给出单独生成token令牌接口,前端要在每次调用时候先获取token令牌。

#### <span id="head6">方案 3: 基于方法粒度指定唯一键【Tomato】</span>

基于Web方法,从参数中寻找可以作为唯一键,进行控制。改造难度低,仅需要服务端改造,前端无感知。



本文是基于方案 3提供的解决方案,具体实现是利用 Redis进行实现。

### <span id="head7"> 三、具体实现原理</span>
原理非常简单,我们为每次请求声明一个防重的时间范围,范围内的重复请求都会当做重复提交被拦截。核心原理就是这么简单。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20200101114303637.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

基于控制时间两种防重策略

#### <span id="head8">策略一: 滑动窗口策略</span>

每次请求设置当前请求的控制时间,控制时间内请求均会被拦截。

![在这里插入图片描述](https://img-blog.csdnimg.cn/20200101114341400.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

#### <span id="head9">**策略二: 固定窗口策略**</span>

仅仅为第一次请求生成一个控制时间,控制时间内相同的请求会被拦截,控制时间过期后,以此类推。
![在这里插入图片描述](https://img-blog.csdnimg.cn/2020010111440842.png?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L01lc3NhZ2VfbHg=,size_16,color_FFFFFF,t_70)

### <span id="head10"> 四、代码演示</span>

#### <span id="head11"> 1.利用拦截器实现</span>

```java
class Example{

        @Repeat
        @GetMapping("/tt")
        public String getUser(@TomatoToken String name) {
            System.out.println(System.currentTimeMillis() + ":" + name);
            String s = System.currentTimeMillis() + ":" + name;
            return s;
        }


        @Repeat
        @PostMapping(value = "/post", consumes = MediaType.APPLICATION_JSON_VALUE)
        public String postUserName(@TomatoToken("userName") @RequestBody UserRequest userRequest) {
            System.out.println(System.currentTimeMillis() + ":" + userRequest.getUserName());
            String s = System.currentTimeMillis() + ":" + userRequest.getUserName();
            return s;
        }

        @Repeat(throwable = NullPointerException.class, message = "禁止重复提交")
        @PostMapping(value = "/form")
        public String postUserName(@TomatoToken("userName") HttpServletRequest userRequest) {
            System.out.println(System.currentTimeMillis() + ":" + userRequest.getParameter("userName"));
            String s = System.currentTimeMillis() + ":" + userRequest.getParameter("userName");
            return s;
        }
        
        
        @PostMapping("/el/phoneNo")
        @Repeat
        public String elName(@TomatoToken("phone.phoneNo") @RequestBody User user) {
             System.out.println(user);
             // 当前TOKEN:17601234466
             return "当前TOKEN:" + StaticContext.getToken();
         }
}
```

**User模型结构**
```json
{

    "name":"Tomato",
    "age": 27,
    "phone":{
        "phoneNo": "17601234466"
    }
}
```

#### <span id="head12"> 2.硬编码实现</span>

```java
class Example{
     
    @Autowired
    private Idempotent idempotent;

    public VoidResponse addWhite(String name) {
  			idempotent.idempotent(name,1000,()->new RuntimeException("重复提交"))
        return VoidResponse.SUCCESS();
    }
}
```

#### <span id="head12"> 3.支持SpringEL表达公式</span>

[SpringEL表达式](https://blog.springlearn.cn/posts/10828/)

``` 
    @Test
    public void testExample() {
        // 表达式解析器
        User liuxin = new User("liuxin", 23,new Phone("123213321"));

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
```


### <span id="head13"> 五、快速使用</span>

![](https://img.springlearn.cn/blog/learn_1578324866000.png)

#### <span id="head14">1. 加入依赖</span>

**Maven**

```
<dependency>
  <groupId>com.github.lxchinesszz</groupId>
  <artifactId>tomato-spring-boot-starter</artifactId>
  <version>1.0.1.RELEASE</version>
</dependency>
```

**Gradle**

```
implementation 'com.github.lxchinesszz:tomato-spring-boot-starter:1.0.1.RELEASE'
```

#### <span id="head15">2. 启用成功</span>

当出现tomato Logo即说明启用成功
![](https://img.springlearn.cn/blog/learn_1578154596000.png)



### <span id="head16"> 六、版本发布记录</span>

**1.0.1-RELEASE**

- 修复 `bug`

**1.0.2-RELEASE**

- `Repeat` 新增拦截策略
- 代码逻辑精简, 增强扩展性
- 增强 `StaticContext` 上下文,支持用户获取令牌值及令牌初始化参数

**1.0.3-RELEASE**

在此感谢@ruansheng与@lianbitangjin关于以下问题的发现,以及给出的建议。
- fix bug "StaticContext.setToken(tomatoToken);"所导致的NPE问题
- 过期时间设置更换原子性API

**1.0.4-RELEASE**

- fix 异常因为aop代理而被吃掉的问题

**1.0.5-RELEASE**

在此感谢@mostcool提出的宝贵建议
- 支持使用el表达式,获取幂等建

**1.0.6-RELEASE**

- 支持SpringEL表达式,兼容原有语法的基础上支持更加丰富的语法
- 代码性能优化

**1.0.7-RELEASE**

再此感谢foot80@163.com提供信息
- 修复Repeat上,@AliasFor注解失效问题

**1.0.8-RELEASE**

- 精确幂等拦截时间,对方法+本次请求加锁,方法锁未失效前,同样拦截。
  解决在耗时方法上进行幂等拦截,请求锁长时间失效,导致拦截失败问题。
- Repeat 新增方法锁耗时,默认(60*1000)ms
- 修改策略,幂等键为空场景直接阻断告警(多半是开发者使用错误导致)

**1.0.9-RELEASE**

- 拦截器注解匹配逻辑优化
- 启动logo支持配置,默认打印
- @TomatoToken注解支持派生,方便开发者对注解进行二次封装

**1.0.10-RELEASE**

- @Repeat增加headValue属性,支持从请求头中获取幂等键（不支持el表达式）


**1.0.11-RELEASE**

- 修复 1.0.10自动化配置失效问题
- 新增 Banner 打印防重模式,RedisCache Or LocalMapCache 
```RedisCache
 _____                      _        
/__   \___  _ __ ___   __ _| |_ ___  
  / /\/ _ \| '_ ` _ \ / _` | __/ _ \ 
 / / | (_) | | | | | | (_| | || (_) |
 \/   \___/|_| |_| |_|\__,_|\__\___/ (Mode: RedisCache)                 

 :: Tomato ::                   (v1.0.11-RELEASE) 

```

``` LocalMapCache
 _____                      _        
/__   \___  _ __ ___   __ _| |_ ___  
  / /\/ _ \| '_ ` _ \ / _` | __/ _ \ 
 / / | (_) | | | | | | (_| | || (_) |
 \/   \___/|_| |_| |_|\__,_|\__\___/ (Mode: LocalMapCache)                 

 :: Tomato ::                   (v1.0.11-RELEASE) 
```

**1.0.12-RELEASE**

- 支持 springboot3.0 自动化配置，同时向下兼容
- 加速网络