package cn.com.zhxj.common.dao.core;

import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * 跟踪对象的更改
 */
public class Traced<T extends Entity> {

    private T        dirtyEntity;
    private T        entity;

    @SuppressWarnings("unchecked")
    private Traced(T entity){
       this.dirtyEntity=(T)entity.copy();
       this.entity=entity;
    }

    public static <T extends Entity> Traced<T>  startTrace(T entity){
        return new Traced<>(entity);
    }


    public T get(){
        return entity;
    }

    public Traced<T> apply(Consumer<T> consumer){
        consumer.accept(entity);
        return this;
    }

    public Map<String,Object> getModifies(){
        Map<String,Object> map=new HashMap<>();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(entity.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            try {
                Method readMethod = propertyDescriptor.getReadMethod();
                Object oldVal = readMethod.invoke(dirtyEntity);
                Object newVal = readMethod.invoke(entity);
                if(!Objects.equals(oldVal,newVal)){
                    map.put(propertyDescriptor.getName(),newVal);
                }
            }  catch (Throwable e) {
               throw new RuntimeException(e.getMessage(),e);
            }
        }
        return map;
    }
}
