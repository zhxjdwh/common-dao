package cn.com.zhxj.common.dao.util;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.core.ExamplePolicy;
import cn.com.zhxj.common.dao.core.Traced;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.impl.PropertyCallIterator;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.EntityMappingFactory;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.core.ExprBuilder;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static cn.com.zhxj.common.dao.core.ExprBuilder.and;
import static cn.com.zhxj.common.dao.core.ExprBuilder.eq;
import static cn.com.zhxj.common.dao.core.ExprFactory.*;

@SuppressWarnings("unchecked")
public class DaoUtils {

    public static final String SELECT_ALL_EXPR = "$SELECT_ALL_EXPR";

    public static <T extends Entity> FieldRefExpr[] getGetterFields(Integer expect) {
        List<PropertyCall> methodCalls = ProxyUtils.getPropertyIterator().allSetter();
        if (expect != null && methodCalls.size() != expect) {
            throw new RuntimeException("不正确的调用方式");
        }
        EntityDesc<T> entityDesc = EntityMappingFactory.getEntityDesc((Class<T>) methodCalls.get(0).getThisObjClass());
        FieldRefExpr[] fieldRefExprs = methodCalls.stream().map(w -> {
            FieldDesc field = entityDesc.getColumnByPropName(w.getPropertyName());
            return $field(field.getColumnName());
        }).toArray(FieldRefExpr[]::new);

        return fieldRefExprs;
    }


    /**
     * @param isSetter 否就是 getter
     * @param <T>
     * @return
     */
    public static <T extends Entity> FieldRefExpr getNextField(boolean isSetter) {
        PropertyCallIterator iterator = ProxyUtils.getPropertyIterator();
        PropertyCall call;
        if (isSetter) {
            call = iterator.nextSetter();
        } else {
            call = iterator.nextGetter();
        }
        if (call == null) {
            return null;
        }
        EntityDesc<T> entityDesc = EntityMappingFactory.getEntityDesc((Class<T>) call.getThisObjClass());
        FieldDesc field = entityDesc.getColumnByPropName(call.getPropertyName());
        return $field(field.getColumnName());
    }

    public static <T extends Entity> boolean hasNextField(boolean isSetter) {
        PropertyCallIterator iterator = ProxyUtils.getPropertyIterator();
        if (isSetter) {
            return iterator.hasMoreSetter();
        } else {
            return iterator.hasMoreGetter();
        }
    }

    public static <T extends Entity> FieldRefExpr[] getGetterFields(Consumer<T> consumer, EntityDesc<T> entityDesc) {
        T proxy = proxy(entityDesc.getEntityClass());
        consumer.accept(proxy);
        return getGetterFields(proxy, entityDesc);
    }


    public static <T extends Entity> FieldRefExpr[] getGetterFields(Consumer<T> consumer, EntityDesc<T> entityDesc, Integer expect) {
        T proxy = proxy(entityDesc.getEntityClass());
        consumer.accept(proxy);
        return getGetterFields(proxy, entityDesc, expect);
    }

    public static <T extends Entity> FieldRefExpr[] getGetterFields(Function<T, ?> func, EntityDesc entityDesc) {
        Consumer<T> consumer = func::apply;
        return getGetterFields(consumer, entityDesc);
    }

    public static <T extends Entity> FieldRefExpr[] getGetterFields(Function<T, ?> func, EntityDesc entityDesc, Integer expect) {
        Consumer<T> consumer = func::apply;
        return getGetterFields(consumer, entityDesc, expect);
    }

    public static FieldRefExpr[] getGetterFields(Object proxyObject, EntityDesc entityDesc, Integer expect) {
        List<PropertyCall> calls = ProxyUtils.getPropertyIterator(proxyObject).allGetter();
        FieldRefExpr[] fields = new FieldRefExpr[calls.size()];
        for (int i = 0; i < calls.size(); i++) {
            if (expect != null && i >= expect) {
                throw new RuntimeException("不正确的调用方式");
            }
            fields[i] = $field(entityDesc.getColumnByPropName(calls.get(i).getPropertyName()).getColumnName());
        }
        return fields;
    }

    public static FieldRefExpr[] getGetterFields(Object proxyObject, EntityDesc entityDesc) {
        return getGetterFields(proxyObject, entityDesc, null);
    }

    public static Expr getIdEqualExpr(Object val, EntityDesc entityDesc){
        FieldDesc[] idColumns = entityDesc.getIdColumns();
        if (idColumns==null ){
            throw new IllegalArgumentException("Id不存在,请检查Id注解");
        }
        if (idColumns.length>1){
            throw new IllegalArgumentException("暂时不支持复合Id");
        }
        return $binary($field(idColumns[0].getColumnName()), ExprBuilder.eq(),$const(val));
    }
    public static FieldDesc[] getIdColumns(EntityDesc entityDesc){
        return entityDesc.getIdColumns();
    }

    public static Expr getExpr(Object proxyObject, EntityDesc entityDesc) {
        Expr expr = null;
        List<PropertyCall> calls = ProxyUtils.getPropertyIterator(proxyObject).allSetter();
        if (calls == null || calls.size() < 1) {
            throw new IllegalArgumentException("不正确的调用方式,请为实体类属性赋值: entity.setXXX(xxx)");
        }
        for (PropertyCall call : calls) {
            FieldDesc column = entityDesc.getColumnByPropName(call.getPropertyName());
            Object value = call.getArgs()[0];
            if (value == null) {
                throw new IllegalArgumentException("不支持null值");
            }
            BinaryExpr binaryExpr = $binary($field(column.getColumnName()), ExprBuilder.eq(), $const(value));
            if (expr == null) {
                expr = binaryExpr;
            } else {
                expr = $logical(expr, ExprBuilder.and(), binaryExpr);
            }
        }
        return expr;
    }

    public static <T extends Entity> Expr getExpr(Consumer<T> example, EntityDesc entityDesc) {
        T proxy = proxy((Class<T>) entityDesc.getEntityClass());
        example.accept(proxy);
        return getExpr(proxy, entityDesc);
    }

    public static <T extends Entity> Expr getExpr(Function<T, Expr> func, EntityDesc entityDesc) {
        T proxy = proxy((Class<T>) entityDesc.getEntityClass());
        return func.apply(proxy);
    }


    public static TableRefExpr getTableRefExpr(EntityDesc entityDesc) {
        return new TableRefExpr(entityDesc.getTableName());
    }


    public static <T extends Entity> Expr getWhereExprFromExample(T example, ExamplePolicy policy, EntityDesc entityDesc) {
       return getWhereExprFromExample(example,policy,entityDesc,null);
    }
    public static <T extends Entity> Expr getWhereExprFromExample(T example, ExamplePolicy policy, EntityDesc entityDesc, Set<String> excludeColNames) {
        Expr expr = null;
        Map<String, Object> params = createParamsFromExample(example, policy);
        if (params == null || params.size() < 1) {
            throw new IllegalArgumentException("请为实体类属性赋值: entity.setXXX(xxx)");
        }
        for (Map.Entry<String, Object> p : params.entrySet()) {
            FieldDesc column = entityDesc.getColumnByPropName(p.getKey());
            if(excludeColNames!=null && excludeColNames.contains(column.getColumnName())){
                continue;
            }
            BinaryExpr binaryExpr = $binary($field(column.getColumnName()), ExprBuilder.eq(), $const(p.getValue()));
            if (expr == null) {
                expr = binaryExpr;
            } else {
                expr = $logical(expr, ExprBuilder.and(), binaryExpr);
            }
        }
        return expr;
    }

    public static Map<String, Object> createParamsFromExample(Object obj, ExamplePolicy policy) {
        Map<String, Object> beanMap = FastBeanUtils.toBeanMap(obj);
        if(policy.equals(ExamplePolicy.INCLUDE_NULL)){
            return beanMap;
        }
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> kv : beanMap.entrySet()) {
            Object val = kv.getValue();
            if(policy.canAccept(val)){
                map.put(kv.getKey(), val);
            }
        }
//
//        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(obj.getClass());
//        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
//            if(propertyDescriptor.getWriteMethod()==null || propertyDescriptor.getReadMethod()==null){
//                continue;
//            }
//            Object val = ReflectionUtils.readProp(obj, propertyDescriptor);
//            switch (policy) {
//                case INCLUDE_NULL:
//                    break;
//                case INCLUDE_NOT_NULL: {
//                    if (val == null) {
//                        continue;
//                    }
//                }
//                break;
//                case EXCLUDE_NULL_EMPTY: {
//                    if (val == null) {
//                        continue;
//                    }
//                    if (val instanceof String && ((String) val).isEmpty()) {
//                        continue;
//                    }
//                }
//                break;
//                case EXCLUDE_NULL_EMPTY_BLANK: {
//                    if (val == null) {
//                        continue;
//                    }
//                    if (val instanceof String) {
//                        String valStr = (String) val;
//                        if (StringUtils.isEmpty(valStr)) {
//                            continue;
//                        }
//                        if (StringUtils.isBlank(valStr)) {
//                            continue;
//                        }
//                    }
//                }
//                break;
//            }
//            map.put(propertyDescriptor.getName(), val);
//        }
        return map;
    }


    public static Map<String, Object> getExecutedSetters(Object proxyObject) {
        Map<String, Object> map = new HashMap<>();
        List<PropertyCall> calls = ProxyUtils.getPropertyIterator(proxyObject).allSetter();
        for (PropertyCall call : calls) {
            map.put(call.getPropertyName(), call.getArgs()[0]);
        }
        return map;
    }



    public static List<PropertyCall> getExecutedSetterList(Object proxyObject) {
        ArrayList<PropertyCall> list = new ArrayList<>();
        List<PropertyCall> calls = ProxyUtils.getPropertyIterator(proxyObject).allSetter();
        for (PropertyCall call : calls) {
            list.add(call);
        }
        return list;
    }


    public static <T extends Entity> List<UpdateExpr.Entry> parseUpdateEntries(Traced<T> traced, EntityDesc entityDesc) {
        List<UpdateExpr.Entry> entries = new ArrayList<>();
        Map<String, Object> modifies = traced.getModifies();
        if (modifies.size() < 1) {
            throw new IllegalArgumentException("Traced对象没有任何更改");
        }
        for (Map.Entry<String, Object> kv : modifies.entrySet()) {
            FieldDesc column = entityDesc.getColumnByPropName(kv.getKey());
            entries.add(new UpdateExpr.Entry($field(column.getColumnName()), $const(kv.getValue())));
        }
        return entries;
    }

    public static List<UpdateExpr.Entry> parseUpdateEntries(Object proxyObject, EntityDesc entityDesc) {
        List<UpdateExpr.Entry> entries = new ArrayList<>();
        List<PropertyCall> calls = ProxyUtils.getPropertyIterator(proxyObject).allSetter();
        if (calls == null || calls.size() < 1) {
            throw new IllegalArgumentException("请为实体类属性赋值: entity.setXXX(xxx)");
        }
        for (PropertyCall call : calls) {
            FieldDesc column = entityDesc.getColumnByPropName(call.getPropertyName());
            entries.add(new UpdateExpr.Entry($field(column.getColumnName()), $const(call.getArgs()[0])));
        }
        return entries;
    }

    public static <T extends Entity> OrderByExpr getOrderByExpr(Function<T, OrderByExpr> expr, EntityDesc entityDesc) {
        if (expr == null) {
            return null;
        }
        T proxy = proxy((Class<T>) entityDesc.getEntityClass());
        return expr.apply(proxy);
    }

    public static <T extends Entity> OrderEntryExpr getOrderEntryExpr(Function<T, OrderEntryExpr> expr, EntityDesc entityDesc) {
        if (expr == null) {
            return null;
        }
        T proxy = proxy((Class<T>) entityDesc.getEntityClass());
        return expr.apply(proxy);
    }

    public static <T extends Entity> SelectExpr getSelectAllExpr(EntityDesc<T> entityDesc) {
        SelectExpr expr = entityDesc.getProperty(SELECT_ALL_EXPR);
        if (expr == null) {
            FieldRefExpr[] fieldRefExprs = entityDesc.getColumns().stream().map(w -> new FieldRefExpr(w.getColumnName())).toArray(FieldRefExpr[]::new);
            expr = new SelectExpr(fieldRefExprs);
            entityDesc.addProperty(SELECT_ALL_EXPR, expr);
        }
        return expr;
    }

    public static <T> T proxy(Class<T> tClass) {
        return ProxyUtils.newProxyObject(tClass);
    }
}
