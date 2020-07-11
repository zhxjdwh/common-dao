package cn.com.zhxj.common.dao.mapping;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class ClassInfo {
    private Class<?>              type;
    private Map<Class<?>, Object> annotations;
    private List<FieldInfo>       fieldInfos;

    public static ClassInfo of(Class cls){
        ClassInfo info = new ClassInfo();
        info.type=cls;
        return info;
    }
}
