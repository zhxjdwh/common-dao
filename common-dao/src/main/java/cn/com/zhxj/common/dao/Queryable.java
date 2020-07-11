package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.expr.Expr;
import cn.com.zhxj.common.dao.expr.OrderByExpr;
import cn.com.zhxj.common.dao.expr.QueryExpr;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public interface Queryable<T extends Entity> {

//    Queryable<T> errorThrow(RuntimeException throwWhenError);

    Queryable<T> whereExpr(Function<T, Expr> where);

    Queryable<T> whereExpr(boolean predicate, Function<T, Expr> where);

    Queryable<T> whereNotNull(Object predicate, Function<T, Expr> where);

    Queryable<T> whereNotEmpty(String predicate, Function<T, Expr> where);

    Queryable<T> whereNotBlank(String predicate, Function<T, Expr> where);

    Queryable<T> where(Consumer<T> where);

    Queryable<T> whereMap(Map<String,Object> where);

    Queryable<T> whereMap(Consumer<Map<String,Object>> where);

    Queryable<T> whereExample(T example);

    Queryable<T> whereExample(T example, ExamplePolicy policy);

    Queryable<T> whereIdEqual(Object id);

    Queryable<T> whereExampleExclude(T example, ExamplePolicy policy, Function<T, List<Object>> excludeFields);

    Queryable<T> orderByFields(Function<T, List<Object>> orderBy, boolean isDesc);

    /**
     * query.orderByAsc(w->list(w.getId()))
     * @param orderBy
     * @return
     */
    Queryable<T> orderByAsc(Function<T, List<Object>> orderBy);

    /**
     * query.orderByDesc(w->list(w.getId()))
     * @param orderBy
     * @return
     */
    Queryable<T> orderByDesc(Function<T, List<Object>> orderBy);

    /**
     *  query.orderByDesc(w->$(w.getId(),desc(),w.getName(),asc()))
     * @param orderByExpr
     * @return
     */
    Queryable<T> orderByExpr(Function<T, OrderByExpr> orderByExpr);

    /**
     * <code>
     *     Map map=new HashMap();
     *     map.put("field name1 or Column Name","desc")
     *     query.orderByMap(map);
     * </code>
     * @param orderByMap
     * @return
     */
    Queryable<T> orderByMap(Map<String, String> orderByMap);

    /**
     * <code>
     *     query.orderByMap(map->{
     *         map.put("field name1 or Column Name","desc")
     *     });
     * </code>
     * @param orderByMap
     * @return
     */
    Queryable<T> orderByMap(Consumer<Map<String, String>> orderByMap);

    Queryable<T> unsafeOrderByMap(Map<String, String> orderByMap);

    /**
     * <code>
     *     query.unsafeOrderByMap(map->{
     *         //这个是不安全的，直接作为sql的一部分,调用方需要自行保证不会被注入
     *         map.put("FD_ID+1","desc")
     *     });
     * </code>
     * @param orderByMap
     * @return
     */
    Queryable<T> unsafeOrderByMap(Consumer<Map<String, String>> orderByMap);

    Queryable<T> select(Function<T, List<Object>> fields);

    Queryable<T> selectOneField(Function<T, Object> field);

    Queryable<T> selectExclude(Function<T, List<Object>> fields);

    Queryable<T> selectDistinct(Function<T, List<Object>> fields);

    <T1> List<Tuple1<T1>> toTuple1(Function<T, Tuple1<T1>> tupleFunc);

    <T1, T2> List<Tuple2<T1, T2>> toTuple2(Function<T, Tuple2<T1, T2>> tupleFunc);

    <T1, T2, T3> List<Tuple3<T1, T2, T3>> toTuple3(Function<T, Tuple3<T1, T2, T3>> tupleFunc);

    <T1, T2, T3, T4> List<Tuple4<T1, T2, T3, T4>> toTuple4(Function<T, Tuple4<T1, T2, T3, T4>> tupleFunc);

    <T1, T2, T3, T4, T5> List<Tuple5<T1, T2, T3, T4, T5>> toTuple5(Function<T, Tuple5<T1, T2, T3, T4, T5>> tupleFunc);

    <T1, T2, T3, T4, T5, T6> List<Tuple6<T1, T2, T3, T4, T5, T6>> toTuple6(Function<T, Tuple6<T1, T2, T3, T4, T5, T6>> tupleFunc);

    <T1, T2, T3, T4, T5, T6, T7> List<Tuple7<T1, T2, T3, T4, T5, T6, T7>> toTuple7(Function<T, Tuple7<T1, T2, T3, T4, T5, T6, T7>> tupleFunc);

    Long count();

    Long countDistinct(Function<T, List<Object>> func);

    List<T> distinct(Function<T, List<Object>> func);

    BigDecimal sum(Function<T,Tuple1<Object>> func);

    BigDecimal avg(Function<T,Tuple1<Object>> func);

    BigDecimal min(Function<T, Tuple1<Object>> func);

    BigDecimal max(Function<T, Tuple1<Object>> func);


    boolean exists();

    T first();

    Optional<T> firstOptional();

    <TField> TField firstSingleField(Function<T,TField> field);

    <TField> Optional<TField> firstSingleFieldOptional(Function<T, TField> field);

    Traced<T> firstForUpdate();

    List<T> top(int limit);

    List<T> toList();

    <Field1,Field2> Map<Field1,Field2> toDistinctMap(Function<T, Tuple2<Field1, Field2>> func);

    Stream<T> toStream();

    List<T> toList(int limit);

    <TField> List<TField> toSingleFieldList(Function<T,TField> field);

    <TField> List<TField> toSingleFieldList(Function<T,TField> field,Integer limit);

    List<Traced<T>> toListForUpdate(int limit);

    Paged<T> toPage(int pageIndex, int pageSize);

    List<T> toPageCountLess(int pageIndex, int pageSize);

    Paged<T> toPageHLLP(int pageIndex, int pageSize, Function<T, String> idFunc);

    QueryExpr getQueryExpr();
}
