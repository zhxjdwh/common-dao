package cn.com.zhxj.common.dao.impl;


import cn.com.zhxj.common.dao.SqlDriverHolder;
import cn.com.zhxj.common.dao.Updatable;
import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.core.ExamplePolicy;
import cn.com.zhxj.common.dao.core.FieldValueHolder;
import cn.com.zhxj.common.dao.core.Traced;
import cn.com.zhxj.common.dao.dialect.SqlCmdType;
import cn.com.zhxj.common.dao.dialect.SqlDriver;
import cn.com.zhxj.common.dao.expr.Expr;
import cn.com.zhxj.common.dao.expr.FieldRefExpr;
import cn.com.zhxj.common.dao.expr.TableRefExpr;
import cn.com.zhxj.common.dao.expr.UpdateExpr;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.util.DaoUtils;
import cn.com.zhxj.common.dao.util.FastBeanUtils;
import cn.com.zhxj.common.dao.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static cn.com.zhxj.common.dao.core.ExprBuilder.$;
import static cn.com.zhxj.common.dao.core.ExprBuilder.and;


/**
 * @param <T>
 */
@Slf4j
public class DefaultUpdatableImpl<T extends Entity> implements Updatable<T> {

    private final Class<T>        entityClass;
    private       SqlDriverHolder sqlDriverHolder;
    private       EntityDesc<T>   entityDesc;

    private Expr                whereExpr;
    private Map<String, Object> updateEntrys = new HashMap<>();
    /**
     * 用于处理bean值回填
     */
    private Map                 fillBeanMap;

    public DefaultUpdatableImpl(EntityDesc<T> entityDesc, SqlDriverHolder sqlDriverHolder) {
        this.entityDesc = entityDesc;
        this.sqlDriverHolder = sqlDriverHolder;
        this.entityClass = entityDesc.getEntityClass();
    }

    private void updateWhereExpr(Expr expr) {
        if (expr == null) {
            return;
        }
        if (whereExpr != null) {
            whereExpr = ExprBuilder.$(whereExpr, ExprBuilder.and(), expr);
            return;
        }
        whereExpr = expr;
    }

    private void set(T entity, ExamplePolicy policy) {
        fillBeanMap = FastBeanUtils.toBeanMap(entity);
        Map<String, Object> map = DaoUtils.createParamsFromExample(entity, policy);
        set(map);
    }

    /**
     * key : field name
     * value
     *
     * @param map
     */
    private void set(Map<String, Object> map) {
        for (Map.Entry<String, Object> kv : map.entrySet()) {
            FieldDesc col = entityDesc.getColumnByPropName(kv.getKey());
            if (col == null) {
                throw new RuntimeException("字段对应的列不存在:" + kv.getKey());
            }
            if (!col.canUpdate()) {
                continue;
            }
            Object val = kv.getValue();
            updateEntrys.put(col.getColumnName(), val);
        }
    }

    @Override
    public Updatable<T> setModified(Traced<T> entity) {
        Map<String, Object> modifies = entity.getModifies();
        set(modifies);
        return this;
    }

    @Override
    public Updatable<T> setAllField(T entity) {
        set(entity, ExamplePolicy.INCLUDE_NULL);
        return this;
    }

    @Override
    public Updatable<T> setNotNull(T entity) {
        set(entity, ExamplePolicy.INCLUDE_NOT_NULL);
        return this;
    }

    @Override
    public Updatable<T> setNotEmpty(T entity) {
        set(entity, ExamplePolicy.EXCLUDE_NULL_EMPTY);
        return this;
    }

    @Override
    public <TField> Updatable<T> setExpr(Function<T, TField> oneField, Function<T,Expr> expr) {
        FieldRefExpr[] getterFields = DaoUtils.getGetterFields(oneField, entityDesc, 1);
        Expr exprRet = DaoUtils.getExpr(expr, entityDesc);
        updateEntrys.put(getterFields[0].getField(), exprRet);
        return this;
    }


    @Override
    public Updatable<T> setNotBlank(T entity) {
        set(entity, ExamplePolicy.EXCLUDE_NULL_EMPTY_BLANK);
        return this;
    }

    @Override
    public Updatable<T> set(Consumer<T> entity) {
        T proxy = DaoUtils.proxy(entityClass);
        entity.accept(proxy);
        Map<String, Object> setters = DaoUtils.getExecutedSetters(proxy);
        set(setters);
        return this;
    }

    @Override
    public Updatable<T> setIf(boolean predicate, Consumer<T> entity) {
        return this;
    }

    @Override
    public Updatable<T> setNotNull(Object predicate, Consumer<T> entity) {
        return setIf(predicate != null, entity);
    }

    @Override
    public Updatable<T> setNotEmpty(String predicate, Consumer<T> entity) {
        return setIf(StringUtils.isNoneEmpty(predicate), entity);
    }

    @Override
    public Updatable<T> setNotBlank(String predicate, Consumer<T> entity) {
        return setIf(StringUtils.isNoneBlank(predicate), entity);
    }

    @Override
    public Updatable<T> setFieldMap(Map<String, Object> fieldMapCaseSentive) {
        set(fieldMapCaseSentive); //字段名是区分大小写的
        return this;
    }

    @Override
    public Updatable<T> setFieldMap(Consumer<Map<String, Object>> colMapIgnoreCase) {
        HashMap<String, Object> map = new HashMap<>();
        colMapIgnoreCase.accept(map);
        return setFieldMap(map);
    }

    @Override
    public Updatable<T> setColumnMap(Map<String, Object> colMapIgnoreCase) {
        HashMap<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Object> kv : colMapIgnoreCase.entrySet()) {
            FieldDesc col = entityDesc.getColumnByColName(kv.getKey()); //列名是不区分大小写的
            if (col == null) throw new IllegalArgumentException("列名不存在，请检查实体注解:" + kv.getKey());
            map.put(col.getPropertyDescriptor().getName(), kv.getValue());
        }
        set(map);
        return this;
    }
    @Override
    public  Updatable<T> setColumnMap(Consumer<Map<String, Object>> colMapIgnoreCase) {
        HashMap<String, Object> map = new HashMap<>();
        colMapIgnoreCase.accept(map);
        return setColumnMap(map);
    }
    @Override
    public <TField> Updatable<T> setOne(Function<T, TField> field, TField value) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(field, entityDesc, 1);
        updateEntrys.put(fields[0].getField(), value);
        return this;
    }

    @Override
    public Updatable<T> where(Consumer<T> where) {
        Expr whereExpr = DaoUtils.getExpr(where, entityDesc);
        updateWhereExpr(whereExpr);
        return this;
    }

    @Override
    public Updatable<T> whereIdEqual(Object id) {
        Expr whereExpr = DaoUtils.getIdEqualExpr(id, entityDesc);
        updateWhereExpr(whereExpr);
        return this;
    }

    @Override
    public Updatable<T> whereExpr(Function<T, Expr> where) {
        Expr whereExpr = DaoUtils.getExpr(where, entityDesc);
        updateWhereExpr(whereExpr);
        return this;
    }

    @Override
    public int execute() {
        if (updateEntrys == null || updateEntrys.size() < 1) {
            throw new RuntimeException("set条件不能为空");
        }
        if (whereExpr == null) {
            throw new RuntimeException("where条件不能为空");
        }
        SqlDriver sqlDriver = sqlDriverHolder.getSqlDriver(entityDesc, SqlCmdType.UPDATE);

        List<FieldDesc> columns = entityDesc.getColumns();
        for (FieldDesc column : columns) {
            if (column.getUpdateConsumer() != null) {
                Object userVal = updateEntrys.get(column.getColumnName());
                FieldValueHolder holder = new FieldValueHolder(entityDesc,
                        column, userVal, updateEntrys.containsKey(column.getColumnName()));
                column.getUpdateConsumer().accept(holder);
                //处理 依靠数据库生成 的字段
                if (holder.getDbGenValue() != null) {
                    String sql = sqlDriver.getDbGenerateSql(entityDesc, column, holder.getDbGenValue());
                    updateEntrys.put(column.getColumnName(), ExprFactory.$sql(sql));
                    continue;
                }
                if (!Objects.equals(userVal, holder.getFieldValue())) {
                    updateEntrys.put(column.getColumnName(), holder.getFieldValue());
                    if (fillBeanMap != null) {
                        //字段回填
                        fillBeanMap.put(column.getPropertyDescriptor().getName(), holder.getFieldValue());
                    }
                    continue;
                }
            }
        }

        List<UpdateExpr.Entry> entries = new ArrayList<>();

        for (Map.Entry<String, Object> kv : updateEntrys.entrySet()) {
            FieldRefExpr fieldRefExpr = ExprFactory.$field(kv.getKey());
            Expr valExpr = (kv.getValue() instanceof Expr) ? (Expr) kv.getValue() : ExprFactory.$const(kv.getValue());
            entries.add(new UpdateExpr.Entry(fieldRefExpr, valExpr));
        }
        TableRefExpr tableRefExpr = DaoUtils.getTableRefExpr(entityDesc);
        UpdateExpr updateExpr = ExprFactory.$update(tableRefExpr, entries, ExprFactory.$where(whereExpr));

        return sqlDriverHolder.getSqlDriver(entityDesc, SqlCmdType.UPDATE).update(updateExpr);
    }

}
