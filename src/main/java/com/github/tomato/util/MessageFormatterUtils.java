package com.github.tomato.util;

import org.slf4j.helpers.MessageFormatter;

/**
 * @author liuxin
 * 2023/2/22 16:05
 */
public class MessageFormatterUtils {

    public static String arrayFormat(String messagePattern, Object... args) {
        return MessageFormatter.arrayFormat(messagePattern, args).getMessage();
    }
}
