package cn.com.zhxj.common.dao.mapping;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.mapping.dialect.JpaEntityMapping;
import cn.com.zhxj.common.dao.mapping.dialect.MybaitsPlusEntityMapping;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class EntityMappingFactory {

    private static Map<Class, EntityDesc> tableMap = new ConcurrentHashMap<>();
    private static List<EntityMapping> entityMappings;

    static {
        entityMappings=new ArrayList<>();
        try {
            entityMappings.add(new JpaEntityMapping());
        } catch (Throwable ignored) {
        }
        try {
            entityMappings.add( new MybaitsPlusEntityMapping());
        } catch (Throwable ignored) {
        }

    }

    public static <T extends Entity> EntityDesc<T>  getEntityDesc(Class<T> cls) {
        EntityDesc entityDesc = tableMap.get(cls);
        if (entityDesc != null) {
            return (EntityDesc<T>)entityDesc;
        }
        return init(cls);
    }

    public static <T extends Entity>  String getTableName(Class<T> cls) {
        EntityDesc entityDesc = getEntityDesc(cls);
        return entityDesc.getTableName();
    }

    public static <T extends Entity> List<FieldDesc> getInsertableFields(Class<T> cls) {
        EntityDesc<T> desc = getEntityDesc(cls);
        List<FieldDesc> list = desc.getColumns().stream().filter(FieldDesc::isInsertable).collect(Collectors.toList());
        return list;
    }

    private static <T extends Entity> EntityDesc<T> init(Class<T> cls) {
        EntityDesc<T> desc = null;
        for (EntityMapping entityMapping : entityMappings) {
            boolean support = entityMapping.isSupport(cls);
            if(support){
                desc=entityMapping.getEntityDesc(cls);
            }
            if(desc!=null) break;
        }

        if(desc==null){
            throw new RuntimeException("无法进行实体映射，请检查类注解以及相关包引用");
        }
        tableMap.put(cls, desc);
        return desc;
    }



}
