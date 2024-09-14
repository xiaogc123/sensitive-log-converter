package com.trhui.log;


import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于对敏感信息进行掩码处理
 * 主要针对的是个人身份信息、邮箱、电话号码、银行卡号和地址名称等敏感信息
 *
 * @author: xiaogc
 * @date: 2024/9/13 16:38
 */
public class MaskUtils {
    // 存储编译过的正则表达式模式，用于高效重复使用
    public static final EnumMap<CustomRegexPattern, Pattern> PATTERN_MAP = new EnumMap<>(CustomRegexPattern.class);

    // 默认构造方法
    public MaskUtils() {
    }

    /**
     * 使用所有预定义的正则表达式模式对内容进行掩码处理
     *
     * @param content 待处理的字符串内容
     * @return 掩码处理后的字符串，如果内容为 null，则返回 null
     */
    public static String maskWithAllPattern(String content) {
        if (null == content) {
            return null;
        }
        StringBuilder result = new StringBuilder(content);
        try {
            CustomRegexPattern[] values = CustomRegexPattern.values();
            Arrays.sort(values, Comparator.comparingInt(CustomRegexPattern::getPriority)); // 按优先级排序
            // 遍历所有预定义的正则表达式模式
            for (CustomRegexPattern customRegexPattern : values) {
                // 获取已编译的正则表达式模式
                Pattern idPattern = PATTERN_MAP.get(customRegexPattern);
                if (idPattern != null) {
                    // 创建一个匹配器
                    Matcher matcher = idPattern.matcher(result);
                    StringBuffer buffer = new StringBuffer();
                    while (matcher.find()) {
                        // 将匹配到的内容替换为指定的替换字符串
                        matcher.appendReplacement(buffer, customRegexPattern.getReplacement());
                    }
                    // 将剩余的内容添加到结果中
                    matcher.appendTail(buffer);
                    // 更新结果
                    result = new StringBuilder(buffer);
                }
            }
            return result.toString();
        } catch (Exception e) {
            return content; // 出错时返回原始内容
        }
    }

    static {
        CustomRegexPattern[] values = CustomRegexPattern.values();
        for (CustomRegexPattern p : values) {
            PATTERN_MAP.put(p, Pattern.compile(p.getPattern()));
        }
    }
}

