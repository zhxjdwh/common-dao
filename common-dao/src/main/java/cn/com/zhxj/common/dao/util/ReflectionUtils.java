package cn.com.zhxj.common.dao.util;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Slf4j
public class ReflectionUtils {
    public static List<Field> getAllFields(Class cls) {
        List<Field> fields = new ArrayList<>();
        List<Class> classes = new ArrayList<>();
        do {
            classes.add(cls);
        } while (null != (cls = cls.getSuperclass()));

        for (Class aClass : classes) {
            Field[] declaredFields = aClass.getDeclaredFields();
            fields.addAll(Arrays.asList(declaredFields));
        }
        return fields;
    }

    public static <T> T  tryCreateInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        }  catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Method findSetter(Field field, Class<?> cls) {
        try {
            String name = field.getName();
            String methodName = "set" + (name.toCharArray()[0] + "").toUpperCase(Locale.ENGLISH) + name.substring(1);
            return cls.getMethod(methodName, field.getType());
        } catch (NoSuchMethodException ignored) {
        }
        return null;
    }

    public static Method findGetter(Field field, Class<?> cls) {
        try {
            String name = field.getName();
            String methodName = "get" + (name.toCharArray()[0] + "").toUpperCase(Locale.ENGLISH) + name.substring(1);
            return cls.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object readProp(Object thisObj, PropertyDescriptor propertyDescriptor) {
        try {
            return propertyDescriptor.getReadMethod().invoke(thisObj);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Object tryReadStaticField(Class cls,String fieldName) {
        try {
            Field declaredField = cls.getDeclaredField(fieldName);
            declaredField.setAccessible(true);
            return declaredField.get(null);
        } catch (NoSuchFieldException ignored) {
        } catch (Throwable e) {
            if(log.isDebugEnabled()){
                log.debug(e.getMessage(),e);
            }
        }
        return null;
    }

    public static LambdaInfo getLambdaInfo(Serializable serializable) {
        try {
            //(Lcn/com/zhxj/data/entity/TdCarbill;Ljava/lang/String;)Ljava/lang/String;
            //(Lcn/com/zhxj/data/entity/TdCarbill;)V

            List<Class<?>> params = new ArrayList<>();

            Method m = serializable.getClass().getDeclaredMethod("writeReplace");
            m.setAccessible(true);
            SerializedLambda sl = (SerializedLambda) m.invoke(serializable);
            String typeString = sl.getInstantiatedMethodType();
            String[] types = typeString.split(";");
            for (int i = 0; i < types.length; i++) {
                String type = types[i];
                if (type.startsWith(")")) {
                    break;
                }
                type = StringUtils.stripStart(type, "(");
                type = StringUtils.stripStart(type, "L").replace("/", ".");
                params.add(Class.forName(type));
            }
            return new LambdaInfo(sl, params);
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 把 二维数组 变成 1维 数组
     * 转成List对象
     *
     * @param object
     * @return
     */
    public static Object flatSubArray(Object object) {
        if (object == null) {
            return object;
        }
        if (!object.getClass().isArray()) {
            return object;
        }

        int length1 = Array.getLength(object);
        for (int i = 0; i < length1; i++) {
            Object obj = Array.get(object, i);
            ;
            if (obj.getClass().isArray()) {
                int length = Array.getLength(obj);
                ArrayList<Object> list = new ArrayList<>();
                for (int m = 0; m < length; m++) {
                    Object arrayElement = Array.get(obj, m);
                    list.add(arrayElement);
                }
                Array.set(object, i, list);
            }
        }
        return object;
    }

    public static Object primitiveArrayToList(Object object) {
        if (object == null) {
            return object;
        }
        if (!object.getClass().isArray()) {
            return object;
        }

        int length = Array.getLength(object);
        ArrayList<Object> list = new ArrayList<>();
        for (int m = 0; m < length; m++) {
            Object arrayElement = Array.get(object, m);
            list.add(arrayElement);
        }
        return list;
    }


}
