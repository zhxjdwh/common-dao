package cn.com.zhxj.common.dao.mapping;

import java.lang.reflect.Field;
import java.util.Map;

public class FieldInfo {
    private Field                 field;
    private Map<Class<?>, Object> annotations;
}
