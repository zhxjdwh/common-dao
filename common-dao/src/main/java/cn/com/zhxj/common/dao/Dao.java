package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.expr.Expr;
import cn.com.zhxj.common.dao.expr.OrderByExpr;
import cn.com.zhxj.common.dao.test.TdCompany;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Dao<T extends Entity> {

    default void test(){
        Dao<TdCompany> dao=null;
        TdCompany tdCompany = new TdCompany();
        tdCompany.setFd_phone("xx");
        tdCompany.setFd_del(0);
        int row = dao.insert(tdCompany);
    }

    /**
     * insert 1.id会回填
     * @param entity
     * @return
     */
    int insert(T entity);

    /**
     * insert 1.id会回填
     * @param entity
     * @return
     */
    int insert(Consumer<T> entity);

    /**
     * insert 1.id会回填, 只有不为null的字段才会insert,
     * 因此,null字段对应的数据库列必须是可为null值 或者 有默认值
     * @param entity
     * @return
     */
    int insertNotNull(T entity);

    /**
     * insert 1.id会回填, 只有不为null的字段才会insert,
     * 因此,null字段对应的数据库列必须是可为null值 或者 有默认值
     * @param entity
     * @return
     */
    int insertNotNull(Consumer<T> entity);

    /**
     * 批量insert,注意，批量insert不会回填由数据库生成的id
     * @param entities
     */
    void insertBatch(List<T> entities);

    /**
     * 批量insert,注意，批量insert不会回填由数据库生成的id
     * @param entities
     */
    void insertBatch(Consumer<List<T>> entities);

    int delete(Consumer<T> example);

    <TField> int deleteBy(Function<T, TField> singleField, TField value);

    int deleteById(Object id);

    int deleteWhere(Function<T, Expr> where);

    int updateTraced(Traced<T> traced, Consumer<T> where);

    int updateTracedById(Traced<T> traced,Object id);

    int updateWhere(Consumer<T> setFields, Function<T, Expr> where);

    int updateDynamicWhere(Consumer<T> entity, Function<T, Expr> where);

    int updateAllFieldWhere(T entity, Function<T, Expr> where);

    int updateNotNullWhere(T entity, Function<T, Expr> where);

    int updateNotNullById(T entity, Object id);

    int updateAllFieldById(T entity, Object id);

    int updateDynamic(Consumer<T> entity, Consumer<T> where);

    int updateDynamicById(Consumer<T> entity, Object id);

    int updateAllField(T entity, Consumer<T> where);

    int updateNotNull(T entity, Consumer<T> where);

    void updateBatch(List<T> entities);

    List<T> findLimit(Consumer<T> where, Function<T, OrderByExpr> orderBy, int limit);

    List<T> findLimit(Consumer<T> where, int limit);

    T findById(Object id);

    Traced<T> findByIdForUpdate(Object id);

    List<T> find(Consumer<T> where);

    T findFirst(Consumer<T> where);

    Optional<T> findFirstOptional(Consumer<T> where);

    List<T> findExpr(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int limit);

    List<T> findExpr(Function<T, Expr> where, int limit);

    List<T> findExpr(Function<T, Expr> where);

    Optional<T> findExprFirstOptional(Function<T, Expr> where);

    T findExprFirst(Function<T, Expr> where);

    List<T> findExample(T example, Function<T, OrderByExpr> orderByExpr, ExamplePolicy policy);

    List<T> findExample(T example, ExamplePolicy policy);

    List<T> findExample(T example);

    T findFirstByExample(T example);

    List<Traced<T>> findExprForUpdate(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int limit);

    List<Traced<T>>findExprForUpdate(Function<T, Expr> where, int limit);

    Traced<T> findExprFirstForUpdate(Function<T, Expr> where);

    List<Traced<T>> findForUpdate(Consumer<T> where, Function<T, OrderByExpr> orderBy, int limit);

    List<Traced<T>> findForUpdate(Consumer<T> where, int limit);

    Traced<T> findFirstForUpdate(Consumer<T> where);

    Queryable<T> query();
    Queryable<T> where(Consumer<T> where);
    Queryable<T> whereExpr(Function<T, Expr> where);

    Updatable<T> update();

    Paged<T> findPage(Consumer<T> where, Function<T, OrderByExpr> orderBy, int pageIndex, int pageSize);

    Paged<T> findPageExpr(Function<T, Expr> where, Function<T, OrderByExpr> orderBy, int pageIndex, int pageSize);
}
