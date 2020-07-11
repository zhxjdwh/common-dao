package cn.com.zhxj.common.dao.util;


import cn.com.zhxj.common.dao.impl.PropertyCallIterator;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 记录代理对象 执行的方法记录
 */
public class ProxyUtils {

    private static ThreadLocal<Object> proxy = new ThreadLocal<>();

    @SuppressWarnings("unchecked")
    public static <T> T newProxyObject(Class<?> cls) {
        try {
            Enhancer e = new Enhancer();
            e.setSuperclass(cls);
            e.setCallback(new Interceptor(cls, true, true));
            e.setUseCache(true);
            return (T) e.create();
        } catch (Throwable ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    public static PropertyCallIterator getPropertyIterator() {
        Object o = proxy.get();
        return getPropertyIterator(o);
    }

    public static PropertyCallIterator getPropertyIterator(Object proxyObj) {
        try {
            Object[] cbs = (Object[]) proxyObj.getClass().getDeclaredMethod("getCallbacks").invoke(proxyObj);
            return ((Interceptor) cbs[0]).getPropertyCallIterator();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void clean() {
        proxy.remove();
    }


    private static class Interceptor implements MethodInterceptor {
        private PropertyDescriptor[] propertyDescriptors;
        private Class<?>             cls;
        private List<PropertyCall>   callList;
        private boolean              traceSetter;
        private boolean              traceGetter;
        private PropertyCallIterator propertyCallIterator;

        public Interceptor(Class<?> cls, boolean traceSetter, boolean traceGetter) {
            this.propertyDescriptors = BeanUtils.getPropertyDescriptors(cls);
            this.cls = cls;
            this.traceSetter = traceSetter;
            this.traceGetter = traceGetter;
            this.callList = new ArrayList<>();
            this.propertyCallIterator = new PropertyCallIterator(callList);
        }

        public PropertyCallIterator getPropertyCallIterator() {
            return propertyCallIterator;
        }


        @Override
        public Object intercept(Object proxyObj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            proxy.set(proxyObj);
            PropertyDescriptor propertyDescriptor = getProp(method,traceGetter,traceSetter);
            if(propertyDescriptor==null) return null;
            PropertyCall propertyCall = new PropertyCall(proxyObj, cls, method, args, propertyDescriptor);
            callList.add(propertyCall);
            return null;
        }

        private PropertyDescriptor getProp(Method method,boolean getter,boolean setter) {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                if(getter){
                    if (propertyDescriptor.getReadMethod() != null
                            && propertyDescriptor.getReadMethod().getName().equals(method.getName())) {
                        return propertyDescriptor;
                    }
                }
                if(setter){
                    if (propertyDescriptor.getWriteMethod() != null
                            && propertyDescriptor.getWriteMethod().getName().equals(method.getName())) {
                        return propertyDescriptor;
                    }
                }

            }
            return null;
        }


        private Object invokeSuper(Object proxyObj, Object traceObj, Method method, Object[] args, MethodProxy methodProxy) {
            try {
                if (traceObj != null) {
                    return methodProxy.invoke(traceObj, args);
                }
                return methodProxy.invokeSuper(proxyObj, args);
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable.getMessage(), throwable);
            }
        }
    }


    private static class FieldIterator implements Iterator<PropertyCall> {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public PropertyCall next() {
            return null;
        }
    }
}
