package cn.com.zhxj.common.dao.impl;


import cn.com.zhxj.common.dao.Dao;
import cn.com.zhxj.common.dao.Queryable;
import cn.com.zhxj.common.dao.SqlDriverHolder;
import cn.com.zhxj.common.dao.Updatable;
import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.dialect.SqlCmdType;
import cn.com.zhxj.common.dao.dialect.SqlDriver;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.EntityMappingFactory;
import cn.com.zhxj.common.dao.util.DaoUtils;
import cn.com.zhxj.common.dao.core.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static cn.com.zhxj.common.dao.core.ExprBuilder.$;
import static cn.com.zhxj.common.dao.core.ExprFactory.*;


/**
 * @param <T>
 */
public class DefaultDaoImpl<T extends Entity> implements Dao<T> {

    private final Class<T>        entityClass;
    private       SqlDriverHolder sqlDriverHolder;
    private       EntityDesc<T>   entityDesc;
    private       TableRefExpr    tableRefExpr;

    public DefaultDaoImpl(Class<T> entityClass, SqlDriverHolder sqlDriverHolder) {
        this.entityClass = entityClass;
        this.entityDesc = EntityMappingFactory.getEntityDesc(entityClass);
        this.sqlDriverHolder = sqlDriverHolder;
    }


    @Override
    public int insert(T entity) {
        return getSqlDriver(SqlCmdType.INSERT).insert(entity, entityDesc, ExamplePolicy.INCLUDE_NULL);
    }

    @Override
    public int insert(Consumer<T> entity) {
        T t = newInstance();
        entity.accept(t);
        return insert(t);
    }

    @Override
    public int insertNotNull(T entity) {
        return getSqlDriver(SqlCmdType.INSERT).insert(entity, entityDesc, ExamplePolicy.INCLUDE_NOT_NULL);
    }

    @Override
    public int insertNotNull(Consumer<T> entity) {
        T t = newInstance();
        entity.accept(t);
        return insertNotNull(t);
    }

    /**
     * 对于数据数据库生成的id, 批量insert不会回填
     *
     * @param entities
     * @return
     */
    @Override
    public void insertBatch(List<T> entities) {
        getSqlDriver(SqlCmdType.INSERT).insertBatch(entities, entityDesc);
    }

    /**
     * 批量insert,注意，批量insert不会回填由数据库生成的id
     *
     * @param entities
     */
    @Override
    public void insertBatch(Consumer<List<T>> entities) {
        ArrayList<T> list = new ArrayList<>();
        entities.accept(list);
        insertBatch(list);
    }

    @Override
    public int delete(Consumer<T> example) {
        Expr whereExpr = DaoUtils.getExpr(example, getEntityDesc());
        return delete(ExprFactory.$where(whereExpr));
    }

    @Override
    public <TField> int deleteBy(Function<T, TField> singleField, TField value) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(singleField, entityDesc, 1);
        BinaryExpr expr = $(fields[0], ExprBuilder.eq(), value);
        return delete(ExprFactory.$where(expr));
    }

    @Override
    public int deleteById(Object id) {
        Expr where = DaoUtils.getIdEqualExpr(id, entityDesc);
        return delete(ExprFactory.$where(where));
    }


    @Override
    public int deleteWhere(Function<T, Expr> where) {
        Expr whereExpr = DaoUtils.getExpr(where, getEntityDesc());
        return delete(ExprFactory.$where(whereExpr));
    }

    private int delete(WhereExpr where) {
        if (where == null || where.getExpr() == null) {
            throw new IllegalArgumentException("where条件不能为空");
        }
        DeleteExpr deleteExpr = ExprFactory.$delete(getTableRefExpr(), where);
        return getSqlDriver(SqlCmdType.DELETE).delete(deleteExpr);
    }

    @Override
    public int updateTraced(Traced<T> traced, Consumer<T> where) {
        return update().setModified(traced).where(where).execute();
    }

    @Override
    public int updateTracedById(Traced<T> traced, Object id) {
        return update().setModified(traced).whereIdEqual(id).execute();
    }

    @Override
    public int updateWhere(Consumer<T> setFields, Function<T, Expr> where) {
        return update().set(setFields).whereExpr(where).execute();
    }

    @Override
    public int updateDynamicWhere(Consumer<T> entity, Function<T, Expr> where) {
        return update().set(entity).whereExpr(where).execute();
    }

    @Override
    public int updateAllFieldWhere(T entity, Function<T, Expr> where) {
        return update().setAllField(entity).whereExpr(where).execute();
    }

    @Override
    public int updateAllFieldById(T entity, Object id) {
        return update().setAllField(entity).whereIdEqual(id).execute();
    }

    @Override
    public int updateAllField(T entity, Consumer<T> where) {
        return update().setAllField(entity).where(where).execute();
    }

    @Override
    public int updateDynamic(Consumer<T> entity, Consumer<T> where) {
        return update().set(entity).where(where).execute();
    }

    @Override
    public int updateDynamicById(Consumer<T> entity, Object id) {
        return update().set(entity).whereIdEqual(id).execute();
    }

    @Override
    public int updateNotNullWhere(T entity, Function<T, Expr> where) {
        return update().setNotNull(entity).whereExpr(where).execute();
    }

    @Override
    public int updateNotNullById(T entity, Object id) {
        return update().setNotNull(entity).whereIdEqual(id).execute();
    }

    @Override
    public int updateNotNull(T entity, Consumer<T> where) {
        return update().setNotNull(entity).where(where).execute();
    }

    /**
     * 批量update
     * id值不能为null,否则会报错
     *
     * @param entities
     * @return
     */
    @Override
    public void updateBatch(List<T> entities) {
        getSqlDriver(SqlCmdType.UPDATE).batchUpdate(entities, entityDesc);
    }

    public <TField> List<T> findIn(Function<T, TField> singleField, List<TField> inItems) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(singleField, entityDesc, 1);
        BinaryExpr expr = ExprBuilder.$(fields[0], ExprBuilder.in(), inItems);
        return dofind(ExprFactory.$where(expr), null, getSelectAllExpr(), null);
    }

    public <TField> List<T> findIn(Function<T, TField> singleField, TField[] inItems) {
        return findIn(singleField, Arrays.asList(inItems));
    }

    public <TField> List<T> findEqual(Function<T, TField> singleField, TField value) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(singleField, entityDesc, 1);
        BinaryExpr expr = $(fields[0], ExprBuilder.eq(), value);
        return dofind(ExprFactory.$where(expr), null, getSelectAllExpr(), null);
    }

    private List<T> dofind(WhereExpr whereExpr, OrderByExpr orderByExpr, SelectExpr selectExpr, Limited limited) {
        QueryExpr queryExpr = ExprFactory.$(selectExpr, $from(getTableRefExpr()), whereExpr, orderByExpr, null, limited);
        return getSqlDriver(SqlCmdType.QUERY).queryForEntity(queryExpr, getEntityDesc().getRowMapper());
    }

    private List<T> find(Consumer<T> where, Function<T, OrderByExpr> orderBy, Limited limit) {
        return query().where(where).orderByExpr(orderBy).toList(limit.getLimit());
    }

    @Override
    public List<T> findLimit(Consumer<T> where, Function<T, OrderByExpr> orderBy, int limit) {
        return query().where(where).orderByExpr(orderBy).toList(limit);
    }

    @Override
    public List<T> findLimit(Consumer<T> where, int limit) {
        return findLimit(where, null, limit);
    }

    @Override
    public T findById(Object id) {
        return query().whereIdEqual(id).first();
    }
    @Override
    public Traced<T> findByIdForUpdate(Object id) {
        return query().whereIdEqual(id).firstForUpdate();
    }
    @Override
    public List<T> find(Consumer<T> where) {
        return query().where(where).toList();
    }

    @Override
    public T findFirst(Consumer<T> where) {
        return query().where(where).first();
    }

    @Override
    public Optional<T> findFirstOptional(Consumer<T> where) {
        T first = query().where(where).first();
        if (first == null) return Optional.empty();
        return Optional.of(first);
    }

    @Override
    public List<T> findExpr(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int limit) {
        return query().whereExpr(where).orderByExpr(orderBy).toList(limit);
    }

    @Override
    public List<T> findExpr(Function<T, Expr> where, int limit) {
        return query().whereExpr(where).toList(limit);
    }

    @Override
    public List<T> findExpr(Function<T, Expr> where) {
        return query().whereExpr(where).toList();
    }

    @Override
    public Optional<T> findExprFirstOptional(Function<T, Expr> where) {
        T first = query().whereExpr(where).first();
        if (first == null) return Optional.empty();
        return Optional.of(first);
    }

    @Override
    public T findExprFirst(Function<T, Expr> where) {
        return query().whereExpr(where).first();
    }

    @Override
    public List<T> findExample(T example, Function<T, OrderByExpr> orderByExpr, ExamplePolicy policy) {
        return query().whereExample(example, policy).orderByExpr(orderByExpr).toList();
    }

    @Override
    public List<T> findExample(T example, ExamplePolicy policy) {
        return query().whereExample(example, policy).toList();
    }

    @Override
    public List<T> findExample(T example) {
        return query().whereExample(example, ExamplePolicy.INCLUDE_NOT_NULL).toList();
    }

    @Override
    public T findFirstByExample(T example) {
        return query().whereExample(example, ExamplePolicy.INCLUDE_NOT_NULL).first();
    }

    @Override
    public List<Traced<T>> findExprForUpdate(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int limit) {
        return query().whereExpr(where).orderByExpr(orderBy).toListForUpdate(limit);
    }

    @Override
    public List<Traced<T>> findExprForUpdate(Function<T, Expr> where, int limit) {
        return query().whereExpr(where).toListForUpdate(limit);
    }

    @Override
    public Traced<T> findExprFirstForUpdate(Function<T, Expr> where) {
        List<Traced<T>> list = query().whereExpr(where).toListForUpdate(1);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public List<Traced<T>> findForUpdate(Consumer<T> where, Function<T, OrderByExpr> orderBy, int limit) {
        return query().where(where).orderByExpr(orderBy).toListForUpdate(limit);
    }

    @Override
    public List<Traced<T>> findForUpdate(Consumer<T> where, int limit) {
        return query().where(where).toListForUpdate(limit);
    }

    @Override
    public Traced<T> findFirstForUpdate(Consumer<T> where) {
        return query().where(where).firstForUpdate();
    }

    @Override
    public Queryable<T> query() {
        return new DefaultQueryableImpl<T>(entityDesc, sqlDriverHolder);
    }

    @Override
    public Queryable<T> where(Consumer<T> where) {
        return query().where(where);
    }

    @Override
    public Queryable<T> whereExpr(Function<T, Expr> where) {
        return query().whereExpr(where);
    }

    @Override
    public Updatable<T> update() {
        return new DefaultUpdatableImpl<>(entityDesc, sqlDriverHolder);
    }

    @Override
    public Paged<T> findPage(Consumer<T> where, Function<T, OrderByExpr> orderBy, int pageIndex, int pageSize) {
        return query().where(where).orderByExpr(orderBy).toPage(pageIndex, pageSize);
    }

    @Override
    public Paged<T> findPageExpr(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int pageIndex, int pageSize) {
        return query().whereExpr(where).orderByExpr(orderBy).toPage(pageIndex, pageSize);
    }

    private SqlDriver getSqlDriver(SqlCmdType sqlCmdType) {
        return sqlDriverHolder.getSqlDriver(entityDesc, sqlCmdType);
    }

    private SelectExpr getSelectAllExpr() {
        return DaoUtils.getSelectAllExpr(getEntityDesc());
    }

    private TableRefExpr getTableRefExpr() {
        if (tableRefExpr != null) {
            return tableRefExpr;
        }
        EntityDesc entityDesc = getEntityDesc();
        return tableRefExpr = new TableRefExpr(entityDesc.getTableName());
    }


    private EntityDesc<T> getEntityDesc() {
        if (entityDesc != null) {
            return entityDesc;
        }
        return entityDesc = EntityMappingFactory.getEntityDesc(entityClass);
    }

    @SuppressWarnings("unchecked")
    private T newInstance() {
        return (T) getEntityDesc().newEntityInstance();
    }

}
