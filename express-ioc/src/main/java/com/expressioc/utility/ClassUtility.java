package com.expressioc.utility;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class ClassUtility {

    private static final ImmutableSet<Class<?>> WRAPPER_TYPES = ImmutableSet.<Class<?>>builder()
            .add(Boolean.class)
            .add(Boolean.class)
            .add(Character.class)
            .add(Byte.class)
            .add(Short.class)
            .add(Integer.class)
            .add(Long.class)
            .add(Float.class)
            .add(Double.class)
            .add(Void.class)
            .add(String.class)
            .build();

    private static final Map<Class, String> BASIC_TYPES = ImmutableMap.<Class, String>builder()
            .put(int.class, "parseInt")
            .put(double.class, "parseDouble")
            .put(float.class, "parseFloat")
            .put(boolean.class, "parseBoolean")
            .put(short.class, "parseShort")
            .put(long.class, "parseLong")
            .put(byte.class, "parseByte")
            .build();

    private static final Map<Class, Class<?>> BASIC_WRAPPER_MAPPING = ImmutableMap.<Class, Class<?>>builder()
            .put(int.class, Integer.class)
            .put(double.class, Double.class)
            .put(float.class, Float.class)
            .put(boolean.class, Boolean.class)
            .put(short.class, Short.class)
            .put(long.class, Long.class)
            .put(byte.class, Byte.class)
            .put(char.class, Character.class)
            .build();

    public static Boolean isBasicType(Class<?> type) {
        return BASIC_TYPES.containsKey(type) || WRAPPER_TYPES.contains(type);
    }

    public static <T> T assembleParameter(String value, Class<T> type) throws Exception {
        if (value.isEmpty() && type != String.class) {
            value = "0";
        }

        if (type.equals(Character.class) || type.equals(char.class)) {
            return (T) Character.valueOf(value.charAt(0));
        }

        if (WRAPPER_TYPES.contains(type)) {
            return parseObjectFromString(value, type);
        } else if (BASIC_TYPES.containsKey(type)) {
            Class<?> wrapperClazz = BASIC_WRAPPER_MAPPING.get(type);
            Method method = wrapperClazz.getDeclaredMethod(BASIC_TYPES.get(type), value.getClass());
            if (method != null) {
                return (T) method.invoke(null, value);
            }
        }
        return null;
    }

    public static <T> T parseObjectFromString(String value, Class<T> clazz) throws Exception {
        return clazz.getConstructor(new Class[]{String.class}).newInstance(value);
    }

    public static Object newInstanceOf(Class paramClazz) {
        try {
            Constructor cons = paramClazz.getDeclaredConstructor();
            return cons.newInstance();
        } catch (NoSuchMethodException e) {
        } catch (InvocationTargetException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        return null;
    }

    public static ImmutableSet<ClassPath.ClassInfo> getTopLevelClassesOf(String packageToAutoRevealSingleImplementation1) {
        try {
            ClassPath classpath = ClassPath.from(ClassLoader.getSystemClassLoader());
            return classpath.getTopLevelClassesRecursive(packageToAutoRevealSingleImplementation1);
        } catch (IOException e) {
            return ImmutableSet.of();
        }
    }
}
