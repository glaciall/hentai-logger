## hentai-logger
一般情况下，我们会在不同的布署环境下，为应用程序配置不同的日志级别。通常是，开发时使用debug级别，而生产环境使用error级别，这都是因为debug输出的日志太多，而绝大部分问题在开发阶段都已经处理好了，在生产环境上，不再有太多的需要调试的情景。但是，一旦发生问题了，我们想要去跟踪日志，而这时应用程序又正处于error日志级别时，这就很难办了，只能修改配置，重启应用程序才能解决了。

**hentai-logger**就是为了解决这种情景的问题而开发的，在配置**log4j**时，我们可以设置多个`Appender`，而每个`Appender`拥有不同的日志输出级别，**error**级别的日志还是输出到滚动日志文件里去，而**debug**级别的日志输出到`cn.org.hentai.logger.core.PhantomAppender`里。

在`cn.org.hentai.logger.core.PhantomAppender`初始化时，将监听于**1122**端口，当有网络连接到此端口时，`PhantomAppender`将系统产生的实时日志写入到网络连接的对方，而当没有连接时，系统产生的日志将全部丢弃掉，从而达到想看日志随时可以看，而又不再额外占用存储空间的目的。

### 依赖
* java 1.6（其实无所谓了，没有用什么最新的特性）
* log4j，也不知道有没有版本兼容问题
* 其它就没有了，没有使用任何第三方组件

### log4j配置说明
```properties
platform.name=hentai
system.name=hentai-logger

# 设置日志Appender，E为下边定义的额外的Appender
log4j.rootLogger = DEBUG, Console, E

# 设置PhantomAppender
log4j.appender.Console = cn.org.hentai.logger.core.PhantomAppender
log4j.appender.Console.layout = org.apache.log4j.PatternLayout
log4j.appender.Console.threshold = DEBUG
log4j.appender.Console.layout.ConversionPattern = [${platform.name}] %d{yyyy-MM-dd HH:mm:ss,SSS} [%-5p] [${system.name}] - %c{1} - %m%n

# 设置名称为E的配置项
log4j.appender.E = org.apache.log4j.ConsoleAppender
log4j.appender.E.layout = org.apache.log4j.PatternLayout
log4j.appender.E.threshold = ERROR
log4j.appender.E.layout.ConversionPattern = [${platform.name}] %d{yyyy-MM-dd HH:mm:ss,SSS} [%-5p] [${system.name}] - %c{1} - %m%n

log4j.logger.cn.org.hentai = DEBUG
```
### 使用方法
直接复制所有类即可。

### 测试类及使用
1. 启动`cn.org.hentai.logger.app.TestApp`，它将不断的输出日志，每10毫秒一条debug日志，每1秒产生一条error日志。
2. 使用网络客户端，比如`netcat`或是`telnet`连接到1122端口，比如`nc localhost 1122`，将立马能够看到上面的应用程序所产生的日志。


### 性能
* 在开启较低级别的日志时，系统将会产生较多的日志输出，log4j的日志输出在组建日志内容串时有一部分性能损耗，在写入到`OutputStream`时也有一部分损耗，本`Appender`只会在有连接时，才会产生完全的性能损耗，平时只会因日志文本格式化而发生损耗。
* 本`Appender`始终维持两个线程，一个用于监听连接，一个用于分发日志内容到各连接。
* 在我的电脑（i7 6700k、32G、三星SSD 970 EVO Plus）下以全速输出日志，CPU占用20%，10秒钟产生了62M的日志内容。
* 不要在网络状况很差的情况下连接到日志输出监听端口，尽量只在本机进行连接，避免带宽占用及网络延迟所造成的消息堆积，以及尽量不要有太多的连接。