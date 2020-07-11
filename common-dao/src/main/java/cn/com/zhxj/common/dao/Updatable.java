package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.core.Traced;
import cn.com.zhxj.common.dao.expr.Expr;

import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Updatable<T extends Entity> {

    /**
     * update 被修改的字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao=null;
     *   Traced<TdCompany> traced = companyDao.findFirstForUpdate(w -> w.setFd_id(100L));
     *   TdCompany company = traced.get();
     *   company.setFd_erpcode("testerpcode");  //被修改的字段
     *   int row = companyDao.update()
     *             .setModified(traced)         //对于没有修改的字段，将会被忽略
     *             .whereIdEqual(company.getFd_id())
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setModified(Traced<T> entity);

    /**
     * update 所有字段(除了id外),如果字段值为null，update到数据库也是null
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");
     *   int row = companyDao.update()
     *             .setAllField(company)         //所有字段(除了id外)都会被update,不管 值有没有被修改
     *             .whereExpr(w->$(w.getFd_id(),eq(),company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setAllField(T entity);

    /**
     * update 所有字段非null字段(除了id外),如果字段值为null，将会被忽略
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");
     *   int row = companyDao.update()
     *             .setNotNull(company)
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setNotNull(T entity);

    /**
     * update 所有字段非null字段或者字符串isNotEmpty(除了id外),如果字段值为null或者empty String，将会被忽略
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");  //将会被update
     *   company.setFd_phone(""); //不会被update
     *   int row = companyDao.update()
     *             .setNotEmpty(company)
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setNotEmpty(T entity);

    <TField> Updatable<T> setExpr(Function<T,TField> oneField,Function<T, Expr> expr);

    /**
     * update 所有字段非null字段或者字符串isNotBlank(除了id外),如果字段值为null或者empty String或者 blank string，将会被忽略
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   TdCompany company = companyDao.query()
     *                 .whereIdEqual(100L)
     *                 .first();
     *   company.setFd_erpcode("testerpcode");  //将会被update
     *   company.setFd_phone("   "); //不会被update
     *   int row = companyDao.update()
     *             .setNotBlank(company)
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> setNotBlank(T entity);

    /**
     * update 指定字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .set(w->{
     *                 //只会update这3个字段
     *                 w.setFd_erpcode("000");
     *                 w.setFd_phone(null);  //将会update为null
     *                 w.setFd_del(1L);
     *             })
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param entity
     * @return
     */
    Updatable<T> set(Consumer<T> entity);

    /**
     * update 指定字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   String phone="xxxx";
     *   int row = companyDao.update()
     *             .setIf(StringUtils.isNotBlank(phone),w->{
     *                 w.setFd_phone(phone);
     *             })
     *             .where(w->w.setFd_id(company.getFd_id()))
     *             .execute();
     * }
     * </pre>
     *
     * @param predicate
     * @param entity
     * @return
     */
    Updatable<T> setIf(boolean predicate, Consumer<T> entity);

    Updatable<T> setNotNull(Object predicate, Consumer<T> entity);

    Updatable<T> setNotEmpty(String predicate, Consumer<T> entity);

    Updatable<T> setNotBlank(String predicate, Consumer<T> entity);

    /**
     * update 多个字段（使用java字段名,区分大小）
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   Map map=new HashMap();
     *   map.put("fd_phone","xxx");
     *   int row = companyDao.update()
     *             .setFieldMap(map)
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param fieldMapCaseSentive
     * @return
     */
    Updatable<T> setFieldMap(Map<String, Object> fieldMapCaseSentive);

    Updatable<T> setFieldMap(Consumer<Map<String, Object>> colMapIgnoreCase);

    /**
     * update 多个字段（使用表列名,不区分大小,表字段必须在要实体类存在）
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   Map map=new HashMap();
     *   map.put("FD_PHONE","xxx");
     *   int row = companyDao.update()
     *             .setColumnMap(map)
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param colMapIgnoreCase
     * @return
     */
    Updatable<T> setColumnMap(Map<String, Object> colMapIgnoreCase);


    Updatable<T> setColumnMap(Consumer<Map<String, Object>> colMapIgnoreCase) ;

    /**
     * update 一个字段
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .setOne(TdCompany::getFd_phone,"xxxx")
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param field
     * @param value
     * @return
     */
    <TField> Updatable<T> setOne(Function<T, TField> field, TField value);

    /**
     * update where
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .set(w->{
     *                 w.setFd_phone("xx");
     *             })
     *             .where(w->w.setFd_id(100L))
     *             .execute();
     * }
     * </pre>
     *
     * @param where
     * @return
     */
    Updatable<T> where(Consumer<T> where);

    /**
     * where Id 等于
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   int row = companyDao.update()
     *             .set(w->{
     *                 w.setFd_phone("xxx");
     *             })
     *             .whereIdEqual(100L)
     *             .execute();
     * }
     * </pre>
     *
     * @param id
     * @return
     */
    Updatable<T> whereIdEqual(Object id);

    /**
     * where 表达式
     * <pre>
     * {@code
     *   Dao<TdCompany> companyDao = null;
     *   String phone="xxxx";
     *   int row = companyDao.update()
     *             .setIf(StringUtils.isNotBlank(phone),w->{
     *                 w.setFd_phone(phone);
     *             })
     *             .whereExpr(w-> $(
     *                    $(w.getFd_id(),eq(),100L) ,
     *                    and() ,
     *                    $(w.getFd_del(),eq(), 1L)
     *             )).execute();
     * }
     * </pre>
     *
     * @param where
     * @return
     */
    Updatable<T> whereExpr(Function<T, Expr> where);

    /**
     * 执行sql
     *
     * @return
     */
    int execute();
}
