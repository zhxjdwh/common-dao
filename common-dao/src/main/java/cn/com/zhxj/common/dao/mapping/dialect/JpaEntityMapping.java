package cn.com.zhxj.common.dao.mapping.dialect;

import cn.com.zhxj.common.dao.mapping.EntityMapping;
import cn.com.zhxj.common.dao.annotation.AutoMapping;
import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.persistence.*;
import java.beans.PropertyDescriptor;
import java.beans.Transient;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class JpaEntityMapping<T extends Entity> implements EntityMapping<T> {

    private static Table newTable(String name, String schema, String catalog) {
        return new Table() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public String catalog() {
                return schema;
            }

            @Override
            public String schema() {
                return catalog;
            }

            @Override
            public UniqueConstraint[] uniqueConstraints() {
                return new UniqueConstraint[0];
            }
        };
    }

    private static Column newColumn(String name) {
        return new Column() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String name() {
                return name;
            }

            @Override
            public boolean unique() {
                return false;
            }

            @Override
            public boolean nullable() {
                return true;
            }

            @Override
            public boolean insertable() {
                return true;
            }

            @Override
            public boolean updatable() {
                return true;
            }

            @Override
            public String columnDefinition() {
                return null;
            }

            @Override
            public String table() {
                return null;
            }

            @Override
            public int length() {
                return 0;
            }

            @Override
            public int precision() {
                return 0;
            }

            @Override
            public int scale() {
                return 0;
            }
        };
    }

    private <T extends Entity> EntityDesc<T> init(Class<T> cls) {
        Table table = cls.getAnnotation(Table.class);
        boolean autoMapping = cls.getAnnotation(AutoMapping.class) != null;
        if (autoMapping) {
            if (table == null) {
                table = newTable(cls.getSimpleName(), null, null);
            }
        }
        List<FieldDesc> columns = getEntityFields(cls, autoMapping);
        EntityDesc<T> desc = new EntityDesc<>(table.name(), table.schema(), cls, columns);
        return desc;
    }

    private List<FieldDesc> getEntityFields(Class<?> cls, boolean isAutoMapping) {
        Map<String, Object> fieldNameMap = new HashMap<>();
        List<FieldDesc> list = new ArrayList<>();
        List<Field> fields = ReflectionUtils.getAllFields(cls);
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(cls);
        for (Field field : fields) {

            if (fieldNameMap.containsKey(field.getName())) {
                continue; //避免名字重复
            }
            fieldNameMap.put(field.getName(), "");
            Transient aTransient = field.getAnnotation(Transient.class);
            if (aTransient != null) {
                continue;
            }

            Column column = field.getAnnotation(Column.class);
            PropertyDescriptor propertyDescriptor = Arrays.stream(propertyDescriptors).filter(w -> w.getName().equals(field.getName())).findFirst().orElse(null);
            if (propertyDescriptor == null) continue;

            GeneratedValue generatedValue = field.getAnnotation(GeneratedValue.class);
            SequenceGenerator sequenceGenerator = field.getAnnotation(SequenceGenerator.class);
            Id id = field.getAnnotation(Id.class);

            if (isAutoMapping) {
                if (column == null) {
                    column = newColumn(field.getName());
                }
            }
            FieldDesc fieldDesc = new FieldDesc();
            fieldDesc.setColumnName(column.name());
            fieldDesc.setInsertable(column.insertable());
            fieldDesc.setUpdatable(column.updatable());
            fieldDesc.setField(field);
            fieldDesc.setFieldType(field.getType());
            fieldDesc.setId(id != null);
            fieldDesc.setAutoIncrement(generatedValue != null && GenerationType.IDENTITY.equals(generatedValue.strategy()));
            fieldDesc.setSequenceName(sequenceGenerator != null ? sequenceGenerator.sequenceName() : null);
            fieldDesc.setPropertyDescriptor(propertyDescriptor);

            processField(fieldDesc);
            list.add(fieldDesc);
        }
        return list;
    }

    @Override
    public EntityDesc<T> getEntityDesc(Class<T> tClass) {
        return init(tClass);
    }

    @Override
    public boolean isSupport(Class<T> tClass) {
        try {
            return tClass.getAnnotation(Table.class) != null;
        } catch (Throwable e) {
            if (log.isDebugEnabled()) {
                log.debug(e.getMessage(), e);
            }
        }
        return false;
    }
}
