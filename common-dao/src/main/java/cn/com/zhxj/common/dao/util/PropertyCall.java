package cn.com.zhxj.common.dao.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
public class PropertyCall {
    private Object thisObj;
    private Class<?> thisObjClass;
    private Method method;
    private Object[] args;
    private PropertyDescriptor propertyDescriptor;
    public String getPropertyName(){
        return propertyDescriptor!=null?propertyDescriptor.getName():null;
    }
    public boolean isSetter(){
        return method.getParameterCount()>0;
    }
    public boolean isGetter(){
        return method.getParameterCount()==0;
    }
}
