# 项目名称

本项目是一个用于对日志中的敏感信息进行掩码处理的工具库，支持 `Logback` 和 `Log4j`。通过自定义日志转换器和布局，能够有效地保护日志中的身份证号、银行卡号、手机号等敏感信息，避免泄露风险。

## 功能特性

- **支持敏感信息脱敏**：对身份证号、手机号、银行卡号、邮箱、地址、中文姓名等类型的信息进行脱敏处理。
- **支持 Logback 和 Log4j**：可以在 `Logback` 和 `Log4j` 两大日志框架中使用自定义转换器/布局实现敏感信息保护。
- **灵活的正则模式**：通过配置 `CustomRegexPattern` 类定义不同敏感信息的匹配模式和掩码规则。

## 核心类说明

### 1. `MaskUtils`

`MaskUtils` 是一个工具类，提供了通过所有预定义的正则表达式对内容进行掩码处理的方法。

- **主要方法**：
    - `maskWithAllPattern(String content)`：对传入的字符串进行敏感信息掩码处理。

- **示例**：

```java
String maskedContent = MaskUtils.maskWithAllPattern("身份证号: 110101199001011234, 手机号: 13658596589");
System.out.println(maskedContent);
// 输出: 身份证号: 110101**********34, 手机号: 136****6589
```
### 2. `CustomRegexPattern`
`CustomRegexPattern` 是一个自定义的正则表达式类，用于定义不同敏感信息的匹配模式和掩码规则。
- **主要模式**：
    身份证号：识别并掩码身份证中的出生日期部分。
    手机号：支持大陆、香港、台湾格式。
    银行卡号：显示前 6 位和后 4 位，中间部分掩码。
    邮箱地址、中文姓名、地址等敏感信息。 
- **示例**：
```java
enum CustomRegexPattern {
    // 定义大陆身份证号的匹配模式
    PATTERN_IDENT_NO("PATTERN_IDENT_NO", "(?<!\\w)(\\d{6})(19\\d{2}|20\\d{2})(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])(\\d{2})(\\d{1}[0-9Xx])(?!\\w)", "$1**********$6"),
    // 定义手机号的匹配模式（支持大陆、香港、台湾格式）
    PATTERN_PHONE("PATTERN_PHONE", "(?<!\\w)(1\\d{2})\\d{4}(\\d{4})(?!\\w)|(?<!\\w)(\\d{4})\\d{3}(\\d{3})(?!\\w)|(?<!\\w)(09\\d{2})\\d{4}(\\d{2})(?!\\w)", "$1$3$5****$2$4$6"),
    // 定义银行卡号的匹配模式（支持前 6 位和后 4 位显示）
    PATTERN_CARD_NO("PATTERN_CARD_NO", "(?<!\\w)(62\\d{4})\\d{6,9}(\\d{4})(?!\\w)", "$1******$2"),
    // 其他模式...
}
 
```
### 3. `SensitiveLogDataConverter `(用于 Logback)
`SensitiveLogDataConverter` 是一个自定义的 `Logback` 转换器，用于对日志信息进行脱敏处理。

- **如何使用**： 在 `logback.xml` 中配置如下内容，引入该转换器：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <conversionRule conversionWord="msg" converterClass="com.demo.log.SensitiveLogDataConverter"/>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <encoder>
        <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
    </encoder>
    </appender>
    <root level="info">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
```
- **示例**：
```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackTest {
    private static final Logger logger = LoggerFactory.getLogger(LogbackTest.class);

    public static void main(String[] args) {
        logger.info("身份证号: 110101199001011234, 手机号: 13658596589");
    }
}
```
- **输出结果**：
```text
2024-09-13 16:38:00 INFO LogbackTest - 身份证号: 110101**********34, 手机号: 136****6589

```
### 4. `MaskingLayout` (用于 Log4j2)
`MaskingLayout` 是一个自定义的 `Log4j2` 日志布局，用于脱敏日志中的敏感信息

- **如何使用**： 在 `Log4j2.xml` 中配置如下内容，引入该布局类：

```xml 
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <appenders>
        <console name="Console" target="SYSTEM_OUT">
            <!--自定义脱敏-->
            <MaskingLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS}[%thread] %-5level %logger{36} - %msg%n" />
        </console>
    </appenders>
    <loggers>
        <root>
        <priority value="info"/>
        <appender-ref ref="console"/>
        </root>
    </loggers>
</configuration>
```
- **示例**：
```java
import org.apache.log4j.Logger;

public class Log4jTest {
    private static final Logger logger = Logger.getLogger(Log4jTest.class);

    public static void main(String[] args) {
        logger.info("银行卡号: 6234567890123456789");
    }
}

```
- **输出结果**：
```text
2024-09-13 16:40:00 INFO Log4jTest - 银行卡号: 623456******5678

```
## 集成步骤
### 1. 引入依赖
在 `pom.xml` 中添加必要的依赖：
```xml
<dependencies>
    <!-- Logback 依赖 -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.2.3</version>
    </dependency>
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-core</artifactId>
        <version>1.2.3</version>
    </dependency>
    <!-- 自定义日志库依赖 -->
    <dependency>
        <groupId>com.demo.log</groupId>
        <artifactId>sensitive-log-converter</artifactId>
        <version>0.0.1</version>
    </dependency>
    <!-- 其他相关依赖 -->
    <!-- ... -->
</dependencies>
```
### 配置 Logback 或 Log4j
根据你的日志框架选择相应的配置文件：

对于 **Logback**，在 `logback.xml` 中配置自定义转换器 `SensitiveLogDataConverter`。
对于 **Log4j**，在 `log4j.xml` 中配置自定义布局 `MaskingLayout`。

### 使用测试
通过 Logger 打印包含敏感信息的日志，查看日志输出是否进行了正确的脱敏处理。

## 贡献指南
1. Fork 项目并克隆到本地。
2. 创建您的特性分支 (git checkout -b feature/your-feature)。
3. 提交您的更改 (git commit -am 'Add some feature')。
4. 推送到分支 (git push origin feature/your-feature)。
5. 提交 Pull Request。

## 许可证
该项目采用 MIT License 许可证。