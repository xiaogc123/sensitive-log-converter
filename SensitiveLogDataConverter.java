package com.trhui.log;

import ch.qos.logback.classic.pattern.MessageConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * logback 自定义的日志数据转换器，用于对日志信息进行掩码处理
 *
 * @author: xiaogc
 * @date: 2024/9/13 16:38
 */
public class SensitiveLogDataConverter extends MessageConverter {
    public SensitiveLogDataConverter() {
    }

    public String convert(ILoggingEvent event) {
        String formattedMessage = event.getFormattedMessage();
        return MaskUtils.maskWithAllPattern(formattedMessage);
    }
}