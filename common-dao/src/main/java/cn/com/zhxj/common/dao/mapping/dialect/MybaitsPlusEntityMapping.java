package cn.com.zhxj.common.dao.mapping.dialect;

import cn.com.zhxj.common.dao.mapping.EntityMapping;
import cn.com.zhxj.common.dao.annotation.AutoMapping;
import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.util.ReflectionUtils;
import com.baomidou.mybatisplus.annotation.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import org.springframework.beans.BeanUtils;

import javax.persistence.Table;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
public class MybaitsPlusEntityMapping<T extends Entity> implements EntityMapping<T> {

    private  <T extends Entity> EntityDesc<T> init(Class<T> cls) {
        String tableName = null;
        String schemaName = null;
        TableName table = cls.getAnnotation(TableName.class);
        boolean autoMapping = cls.getAnnotation(AutoMapping.class) != null;

        if (table != null) {
            tableName = table.value();
            schemaName = table.schema();
        }
        if (autoMapping && tableName == null) {
            tableName = cls.getSimpleName();
        }
        List<FieldDesc> columns = getEntityFields(cls, autoMapping);
        EntityDesc<T> desc = new EntityDesc<>(tableName, schemaName, cls, columns);
        return desc;
    }

    private  List<FieldDesc> getEntityFields(Class<?> cls, boolean isAutoMapping) {

        KeySequence keySequence = cls.getAnnotation(KeySequence.class);

        Map<String, Object> fieldNameMap = new HashMap<>();
        List<FieldDesc> list = new ArrayList<>();
        List<Field> fields = ReflectionUtils.getAllFields(cls);
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(cls);
        for (Field field : fields) {

            if (fieldNameMap.containsKey(field.getName())) {
                continue; //避免名字重复
            }
            fieldNameMap.put(field.getName(), "");
            TableId tableId = field.getAnnotation(TableId.class);
            TableField column = field.getAnnotation(TableField.class);
            PropertyDescriptor propertyDescriptor = Arrays.stream(propertyDescriptors).filter(w -> w.getName().equals(field.getName())).findFirst().orElse(null);
            if (propertyDescriptor == null) continue;

            if (isAutoMapping && column == null && tableId == null) {
                column = newColumn(field.getName());
            }

            FieldDesc fieldDesc = new FieldDesc();


            if (column != null) {
                if (!column.exist()) continue;

                boolean updateFill = FieldFill.INSERT_UPDATE.equals(column.fill()) || FieldFill.UPDATE.equals(column.fill());
                boolean insertFill = FieldFill.INSERT.equals(column.fill()) || FieldFill.INSERT.equals(column.fill());

                fieldDesc.setColumnName(column.value());
                fieldDesc.setInsertable(!FieldStrategy.NEVER.equals(column.insertStrategy()));
                fieldDesc.setUpdatable(!FieldStrategy.NEVER.equals(FieldStrategy.NEVER));
                fieldDesc.setField(field);
                fieldDesc.setFieldType(field.getType());
                fieldDesc.setId(false);
                fieldDesc.setSequenceName(null);
                fieldDesc.setPropertyDescriptor(propertyDescriptor);
                fieldDesc.setUpdateFill(updateFill);
                fieldDesc.setInsertFill(insertFill);
            }
            if (tableId != null) {
                fieldDesc.setColumnName(tableId.value());
                fieldDesc.setInsertable(!IdType.AUTO.equals(tableId.type()));
                fieldDesc.setUpdatable(false);
                fieldDesc.setField(field);
                fieldDesc.setFieldType(field.getType());
                fieldDesc.setId(true);
                fieldDesc.setAutoIncrement(IdType.AUTO.equals(tableId.type()));
                fieldDesc.setSequenceName(keySequence != null ? keySequence.value() : null);
                fieldDesc.setPropertyDescriptor(propertyDescriptor);
            }
            processField(fieldDesc);
            list.add(fieldDesc);
        }
        return list;
    }

    private static TableField newColumn(String name) {
        return new TableField() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String value() {
                return name;
            }

            @Override
            public boolean exist() {
                return true;
            }

            @Override
            public String condition() {
                return null;
            }

            @Override
            public String update() {
                return null;
            }

            @Override
            public FieldStrategy insertStrategy() {
                return FieldStrategy.DEFAULT;
            }

            @Override
            public FieldStrategy updateStrategy() {
                return FieldStrategy.DEFAULT;
            }

            @Override
            public FieldStrategy whereStrategy() {
                return null;
            }

            @Override
            public FieldFill fill() {
                return null;
            }

            @Override
            public boolean select() {
                return false;
            }

            @Override
            public boolean keepGlobalFormat() {
                return false;
            }

            @Override
            public JdbcType jdbcType() {
                return null;
            }

            @Override
            public Class<? extends TypeHandler> typeHandler() {
                return null;
            }

            @Override
            public String numericScale() {
                return null;
            }
        };
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
