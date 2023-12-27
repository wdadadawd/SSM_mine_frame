### 前言

​	框架基于Tomcat启动，仿照SSM功能制作框架，只实现了部分SSM中的核心功能，本人未了解过SSM框架的源码，只根据框架的功能，使用自己的思路去完成这些功能，可能有许多不完善的地方，但自身收获很大。

### 框架依赖

```
<dependency>      <!--servlet依赖-->
    <groupId>javax.servlet</groupId>
    <artifactId>javax.servlet-api</artifactId>
    <version>4.0.1</version>
    <scope>provided</scope>
</dependency>
<dependency>      <!--yaml解析工具依赖-->
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.0</version>
    <scope>compile</scope>
</dependency>
<dependency>     <!--java字节码操作与分析依赖-->
    <groupId>org.javassist</groupId>
    <artifactId>javassist</artifactId>
    <version>3.29.2-GA</version>
</dependency>
<build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.2.3</version>
                <configuration>
                    <archive>
                        <manifestEntries>
                            <Class-Path>.</Class-Path>
                        </manifestEntries>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
```

### 测试Demo所用依赖

```
<dependency>       <!--fastjson依赖-->
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.33</version>
</dependency>
<dependency>   <!-- MySQL驱动 -->
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.16</version>
</dependency>
<dependency>    <!-- 连接池 -->
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.16</version>
</dependency>
```

### 1.框架包结构

- ​	com.lsu
  - spring                               ==>  自定义框架
    - Annotation               ==>  自定义注解包
    - base                           ==>  BaseDAO
    - common                    ==> 基础类包
    - enums                        ==> 枚举类包
    - exceptions                 ==> 自定义异常类包
    - Factory                       ==> 工厂类包
    - Filter                           ==> 过滤器包
    - listeners                     ==> 监听器包
    - Springmvc                 ==> 中央处理器包
    - Utils                            ==> 工具类包 
  - test                                     ==>  使用Demo
    - component               ==> 组件注解测试类包
    - controller                  ==> 控制层测试类包
    - dao                             ==> dao层测试类包
    - Filter                           ==> 过滤器类包
    - pojo                            ==> 基础类
    - service                       ==> 事务层测试类包

### 2.框架使用

#### 2.21框架配置文件

1.配置框架的bean和filter组件的扫描路径

2.配置数据库连接信息(连接地址、连接驱动、连接池类、账号、密码)

3.静态资源存放路径配置

#### 2.2.2 框架注解

1.@Component注解:将标记的类注册为组件，可自定义组件名。

2.@Controller注解:将标记的类注册为组件，并标记为Controller，可自定义组件名。

3.@Service注解:将标记的类注册为组件，并标记为Service，可自定义组件名。

4.@Mapper注解:将标记的类注册为组件1，并标记为Mapper，可自定义组件名。

5.@MyWebFilter注解:将标记的类注册为过滤器，可自定义过滤器名、过滤器优先级。

6.@RequestMapping注解:将在@Controller标记类下标记的方法注册为接口，自定义接口、请求方式。

7.@Autowired注解:自动赋值组件中标记该注解的属性(依赖注入)

#### 2.2.3 框架提供的公共类

1.抽象类BaseDAO<T>:用于继承自定义的Mapper，提供基础访问数据库的方法。

#### 2.2.4 其他功能

1.根据配置文件中的数据库连接信息自动连接数据池、无需其他任何操作

2.DispatcherServlet对请求接口的自动执行和调度、对接口返回值做处理。

3.自动配置过滤器按配置的过滤器优先级对接口执行过滤器



### 3.框架架构

![wps1](https://github.com/wdadadawd/SSM_mine_frame/assets/95529255/73cea2e3-fa57-4b1a-8948-85ba697a51c7)
