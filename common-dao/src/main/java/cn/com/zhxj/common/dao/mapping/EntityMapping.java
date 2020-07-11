package cn.com.zhxj.common.dao.mapping;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.handler.ValueHandler;
import cn.com.zhxj.common.dao.util.ReflectionUtils;

import java.lang.annotation.Annotation;

public interface EntityMapping<T extends Entity> {
    EntityDesc<T> getEntityDesc(Class<T> tClass);

    boolean isSupport(Class<T> tClass);

    @SuppressWarnings("unchecked")
    default void processField(FieldDesc column) {
        Annotation[] annotations = column.getField().getAnnotations();
        for (Annotation annotation : annotations) {
            Class<? extends Annotation> annType = annotation.annotationType();
            Object insert = ReflectionUtils.tryReadStaticField(annType, "insert");
            Object update = ReflectionUtils.tryReadStaticField(annType, "update");
            Object where = ReflectionUtils.tryReadStaticField(annType, "where");
            if ((insert instanceof Class) && ValueHandler.class.isAssignableFrom((Class) insert)) {
                ValueHandler valueHandler = ReflectionUtils.tryCreateInstance((Class<ValueHandler>) insert);
                if (valueHandler != null)
                    column.setInsertConsumer(valueHandler::process);
            }
            if ((update instanceof Class) && ValueHandler.class.isAssignableFrom((Class) update)) {
                ValueHandler valueHandler = ReflectionUtils.tryCreateInstance((Class<ValueHandler>) update);
                if (valueHandler != null)
                    column.setUpdateConsumer(valueHandler::process);
            }
            if ((where instanceof Class) && ValueHandler.class.isAssignableFrom((Class) where)) {
                ValueHandler valueHandler = ReflectionUtils.tryCreateInstance((Class<ValueHandler>) where);
                if (valueHandler != null)
                    column.setWhereConsumer(valueHandler::process);
            }
        }
    }
}
