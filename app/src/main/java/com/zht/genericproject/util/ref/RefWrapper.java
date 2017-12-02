package com.zht.genericproject.util.ref;

import java.util.HashMap;

public class RefWrapper {
    public final static  int                         CLASS_STRING = 1;
    public final static  int                         CLASS_INT    = 2;
    public final static  int                         CLASS_DOUBLE = 3;
    public final static  int                         CLASS_FLOAT  = 4;
    public final static  int                         CLASS_LONG   = 5;
    public final static  int                         CLASS_BYTE   = 6;
    public final static  int                         CLASS_BOOLEN = 7;
    private final static HashMap<Class<?>, Class<?>> UNWRAPPER    = new HashMap<Class<?>, Class<?>>();
    private final static HashMap<Class<?>, Class<?>> WRAPPER      = new HashMap<Class<?>, Class<?>>();
    private final static HashMap<Class<?>, Integer> INTWRAPPER   = new HashMap<>();

    static {
        UNWRAPPER.put(Boolean.class, boolean.class);
        UNWRAPPER.put(Byte.class, byte.class);
        UNWRAPPER.put(Character.class, char.class);
        UNWRAPPER.put(Short.class, short.class);
        UNWRAPPER.put(Integer.class, int.class);
        UNWRAPPER.put(Long.class, long.class);
        UNWRAPPER.put(Float.class, float.class);
        UNWRAPPER.put(Double.class, double.class);
        UNWRAPPER.put(Void.class, void.class);

        WRAPPER.put(boolean.class, Boolean.class);
        WRAPPER.put(byte.class, Byte.class);
        WRAPPER.put(char.class, Character.class);
        WRAPPER.put(short.class, Short.class);
        WRAPPER.put(int.class, Integer.class);
        WRAPPER.put(long.class, Long.class);
        WRAPPER.put(float.class, Float.class);
        WRAPPER.put(double.class, Double.class);
        WRAPPER.put(void.class, Void.class);
        WRAPPER.put(String.class, String.class);

        INTWRAPPER.put(String.class, CLASS_STRING);
        INTWRAPPER.put(Integer.class, CLASS_INT);
        INTWRAPPER.put(Double.class, CLASS_DOUBLE);
        INTWRAPPER.put(Float.class, CLASS_FLOAT);
        INTWRAPPER.put(Long.class, CLASS_LONG);
        INTWRAPPER.put(Byte.class, CLASS_BYTE);
        INTWRAPPER.put(Boolean.class, CLASS_BOOLEN);



    }

    public static Class<?> wrapClass(Class<?> clazz) {
        final Class<?> clazzWraped = WRAPPER.get(clazz);
        return clazzWraped == null ? clazz : clazzWraped;
    }

    public static Class<?> unwrapClass(Class<?> clazz) {
        final Class<?> clazzUnwraped = UNWRAPPER.get(clazz);
        return clazzUnwraped == null ? clazz : clazzUnwraped;
    }

    public static boolean isPrimitive(Object obj) {
        if (obj != null) {
            return isPrimitiveClass(obj.getClass());
        } else {
            return false;
        }
    }

    public static boolean isPrimitiveClass(Class<?> clazz) {
        return UNWRAPPER.containsKey(clazz);
    }

    public static boolean isPrimitiveWrapperClass(Class<?> clazz) {
        return WRAPPER.containsKey(clazz);
    }

    public static int intWrapClass(Class<?> clazz) {
        return INTWRAPPER.containsKey(clazz) ? INTWRAPPER.get(clazz) : 0;
    }
}
