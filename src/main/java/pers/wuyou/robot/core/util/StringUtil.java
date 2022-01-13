package pers.wuyou.robot.core.util;

import org.jetbrains.annotations.Nullable;

/**
 * @author wuyou
 */
public class StringUtil {
    private StringUtil() {
    }

    /**
     * 如果字符串是null则返回空字符串
     */
    public static String isNullReturnEmpty(@Nullable String str) {
        if (str == null) {
            return "";
        }
        return str;
    }
}
