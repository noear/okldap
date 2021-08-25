package org.noear.okldap.utils;


/**
 * @author noear
 * @since 1.0
 * */
public class TextUtils {
    /** 是否为空 */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
