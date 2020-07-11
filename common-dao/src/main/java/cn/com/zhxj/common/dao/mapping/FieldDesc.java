package cn.com.zhxj.common.dao.mapping;

import cn.com.zhxj.common.dao.core.FieldValueHolder;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

@Getter
@Setter
public class FieldDesc {
    private String                     columnName;
    private boolean                    id            = false;
    private boolean                    autoIncrement = false;
    private String                     sequenceName;
    private boolean                    insertable    = true;
    private boolean                    updatable     = true;
    private boolean                    updateFill    = false;
    private boolean                    insertFill    = false;
    private Class<?>                   fieldType;
    private Field                      field;
    private PropertyDescriptor         propertyDescriptor;
    private Consumer<FieldValueHolder> updateConsumer;
    private Consumer<FieldValueHolder> insertConsumer;
    private Consumer<FieldValueHolder> whereConsumer;

    public boolean canUpdate() {
        return !id && updatable;
    }

//    private int length=255;
//    private int precision=0;
//    private int scale=0;
//    private boolean unique =false;
//    private boolean nullable =true;
}
