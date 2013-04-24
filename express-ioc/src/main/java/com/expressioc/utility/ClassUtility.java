package com.expressioc.utility;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ClassUtility {
    private static final HashSet<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    private static final Map<Class, String> BASIC_TYPES = getBasicTypes();
    private static final Map<Class, Class<?>> BASIC_WRAPPER_MAPPING = getMappings();

    private static HashSet<Class<?>> getWrapperTypes() {
        HashSet<Class<?>> wrapper = new HashSet<Class<?>>();
        wrapper.add(Boolean.class);
        wrapper.add(Character.class);
        wrapper.add(Byte.class);
        wrapper.add(Short.class);
        wrapper.add(Integer.class);
        wrapper.add(Long.class);
        wrapper.add(Float.class);
        wrapper.add(Double.class);
        wrapper.add(Void.class);
        wrapper.add(String.class);
        return wrapper;
    }

    private static Map<Class, String> getBasicTypes() {
        Map<Class, String> basicTypes = new HashMap<Class, String>();
        basicTypes.put(int.class, "parseInt");
        basicTypes.put(double.class, "parseDouble");
        basicTypes.put(float.class, "parseFloat");
        basicTypes.put(boolean.class, "parseBoolean");
        basicTypes.put(short.class, "parseShort");
        basicTypes.put(long.class, "parseLong");
        basicTypes.put(byte.class, "parseByte");
        basicTypes.put(char.class, "");
        return basicTypes;
    }

    private static Map<Class,Class<?>> getMappings() {
        Map<Class, Class<?>> mappings = new HashMap<Class, Class<?>>();
        mappings.put(int.class, Integer.class);
        mappings.put(double.class, Double.class);
        mappings.put(float.class, Float.class);
        mappings.put(boolean.class, Boolean.class);
        mappings.put(short.class, Short.class);
        mappings.put(long.class, Long.class);
        mappings.put(byte.class, Byte.class);
        return mappings;
    }

    public static Boolean isBasicType(Class<?> type) {
        return BASIC_TYPES.containsKey(type) || WRAPPER_TYPES.contains(type);
    }

    public static <T> T assembleParameter(String value, Class<T> type) throws Exception {
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
            //TODO
        } catch (InvocationTargetException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (InstantiationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IllegalAccessException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return null;
    }
}
