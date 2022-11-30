package com.zlink.common.assertion;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

/**
 * @author zs
 * @date 2022/11/30
 */
public class Asserts {

    private Asserts() {
    }

    public static boolean isNotNull(Object object) {
        return object != null;
    }

    public static boolean isNull(Object object) {
        return object == null;
    }

    public static boolean isNullString(String str) {
        return isNull(str) || str.isEmpty();
    }

    public static boolean isAllNullString(String... str) {
        return Arrays.stream(str).allMatch(Asserts::isNullString);
    }

    public static boolean isNotNullString(String str) {
        return !isNullString(str);
    }

    public static boolean isAllNotNullString(String... str) {
        return Arrays.stream(str).noneMatch(Asserts::isNullString);
    }

    public static boolean isEquals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    public static boolean isEqualsIgnoreCase(String str1, String str2) {
        return (str1 == null && str2 == null) || (str1 != null && str1.equalsIgnoreCase(str2));
    }

    public static boolean isNullCollection(Collection<?> collection) {
        return isNull(collection) || collection.isEmpty();
    }

    public static boolean isNotNullCollection(Collection<?> collection) {
        return !isNullCollection(collection);
    }

    public static boolean isNullMap(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    public static boolean isNotNullMap(Map<?, ?> map) {
        return !isNullMap(map);
    }

    public static void checkNull(Object key, String msg) {
        if (key == null) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNotNull(Object object, String msg) {
        if (isNull(object)) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNullString(String key, String msg) {
        if (isNull(key) || isEquals("", key)) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNullCollection(Collection<?> collection, String msg) {
        if (isNullCollection(collection)) {
            throw new RuntimeException(msg);
        }
    }

    public static void checkNullMap(Map<?, ?> map, String msg) {
        if (isNullMap(map)) {
            throw new RuntimeException(msg);
        }
    }
}
