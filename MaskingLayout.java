package com.trhui.log;

import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.charset.Charset;

/**
 * log4j2 自定义的 `Log4j2` 日志布局，用于脱敏日志中的敏感信息
 *
 * @author: xiaogc
 * @date: 2024/9/14 13:53
 */
@Plugin(name = "MaskingLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE, printObject = true)
public class MaskingLayout extends AbstractStringLayout {

    private final PatternLayout patternLayout;

    // 通过构造函数初始化 patternLayout
    protected MaskingLayout(Charset charset, String pattern) {
        super(charset);
        this.patternLayout = PatternLayout.newBuilder()
                .withPattern(pattern)
                .build();
    }

    @Override
    public String toSerializable(LogEvent event) {
        // 格式化消息并进行脱敏
        String formattedMessage = patternLayout.toSerializable(event);
        return MaskUtils.maskWithAllPattern(formattedMessage);
    }

    // 使用 @PluginFactory 和 @PluginAttribute 注解处理 XML 配置中的 pattern 属性
    @PluginFactory
    public static MaskingLayout createLayout(
            @PluginAttribute(value = "pattern", defaultString = "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n") String pattern,
            @PluginAttribute(value = "charset", defaultString = "UTF-8") Charset charset) {
        return new MaskingLayout(charset, pattern);
    }
}
