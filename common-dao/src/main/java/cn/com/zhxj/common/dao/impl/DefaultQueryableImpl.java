package cn.com.zhxj.common.dao.impl;

import cn.com.zhxj.common.dao.*;
import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.GroupedQueryable;
import cn.com.zhxj.common.dao.Queryable;
import cn.com.zhxj.common.dao.dialect.SqlCmdType;
import cn.com.zhxj.common.dao.dialect.SqlDriver;
import cn.com.zhxj.common.dao.SqlDriverHolder;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.util.DaoUtils;
import cn.com.zhxj.common.dao.util.HLLPUtils;
import cn.com.zhxj.common.dao.util.PropertyCall;
import cn.com.zhxj.common.dao.core.*;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.com.zhxj.common.dao.core.ExprFactory.*;

public class DefaultQueryableImpl<T extends Entity> implements Queryable<T> {
    private Expr                 whereExpr;
    private List<OrderEntryExpr> orderEntryExprs;
    private List<Expr>           selectExprEntrys;
    private EntityDesc<T>        entityDesc;
    private boolean              selectDistinct = false;
    private Limited              limited;
    private Pagination           pagination;
    private SqlDriverHolder      sqlDriverHolder;
    private RuntimeException     throwWhenError;

    public DefaultQueryableImpl(EntityDesc<T> entityDesc, SqlDriverHolder sqlDriverHolder) {
        this.entityDesc = entityDesc;
        this.sqlDriverHolder = sqlDriverHolder;
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

    private void updateSelectEntrys(Expr expr) {
        if (expr == null) {
            return;
        }
        if (selectExprEntrys == null) {
            selectExprEntrys = new ArrayList<>();
        }
        selectExprEntrys.add(expr);
    }


    private void addOrderEntry(OrderEntryExpr expr) {
        if (expr == null) {
            return;
        }
        if (orderEntryExprs == null) {
            orderEntryExprs = new ArrayList<>();
        }
        orderEntryExprs.add(expr);
    }

    @Override
    public Queryable<T> whereExpr(Function<T, Expr> where) {
        Expr expr = DaoUtils.getExpr(where, entityDesc);
        updateWhereExpr(expr);
        return this;
    }
    @Override
    public Queryable<T> whereExpr(boolean predicate, Function<T, Expr> where) {
        if(!predicate){
            return this;
        }
        Expr expr = DaoUtils.getExpr(where, entityDesc);
        updateWhereExpr(expr);
        return this;
    }
    @Override
    public Queryable<T> whereNotNull(Object predicate, Function<T, Expr> where) {
        if (predicate == null) {
            return this;
        }
        return whereExpr(where);
    }
    @Override
    public Queryable<T> whereNotEmpty(String predicate, Function<T, Expr> where) {
        if (StringUtils.isEmpty(predicate)) {
            return this;
        }
        return whereExpr(where);
    }
    @Override
    public Queryable<T> whereNotBlank(String predicate, Function<T, Expr> where) {
        if (StringUtils.isBlank(predicate)) {
            return this;
        }
        return whereExpr(where);
    }



    @Override
    public Queryable<T> where(Consumer<T> where) {
        Expr expr = DaoUtils.getExpr(where, entityDesc);
        updateWhereExpr(expr);
        return this;
    }

    @Override
    public Queryable<T> whereMap(Map<String, Object> where) {
        for (Map.Entry<String, Object> kv : where.entrySet()) {
            String name = kv.getKey().toLowerCase(Locale.US);
            FieldDesc col = entityDesc.getColumnByColName(name);
            if(col==null){
                col=entityDesc.getColumnByPropName(name);
            }
            if(col==null){
                throw new RuntimeException("字段名或列名不存在:"+kv.getKey());
            }
            BinaryExpr binaryExpr = ExprFactory.$binary($field(col.getColumnName()), ExprBuilder.eq(), ExprFactory.$const(kv.getValue()));
            updateWhereExpr(binaryExpr);
        }
        return this;
    }

    @Override
    public Queryable<T> whereMap(Consumer<Map<String, Object>> where) {
        HashMap<String, Object> map = new HashMap<>();
        where.accept(map);
        return whereMap(map);
    }

//    @Override
//    public Queryable<T> unsafeWhereMap(Map<String, Object> where) {
//        for (Map.Entry<String, Object> kv : where.entrySet()) {
//            BinaryExpr binaryExpr = $binary($sql(kv.getKey()), eq(), $const(kv.getValue()));
//            updateWhereExpr(binaryExpr);
//        }
//        return this;
//    }

    /**
     * 默认不包含null值
     * @param example
     * @return
     */
    @Override
    public Queryable<T> whereExample(T example) {
        return whereExample(example, ExamplePolicy.INCLUDE_NOT_NULL);
    }
    /**
     * 默认不包含null值
     * @param example
     * @return
     */
    @Override
    public Queryable<T> whereExample(T example,ExamplePolicy policy) {
        Expr where = DaoUtils.getWhereExprFromExample(example, policy, entityDesc);
        updateWhereExpr(where);
        return this;
    }

    @Override
    public Queryable<T> whereIdEqual(Object id) {
        Expr whereExpr = DaoUtils.getIdEqualExpr(id,entityDesc);
        updateWhereExpr(whereExpr);
        return this;
    }

    @Override
    public Queryable<T> whereExampleExclude(T example,ExamplePolicy policy,Function<T,List<Object>> excludeFields) {
        FieldRefExpr[] exculdes = DaoUtils.getGetterFields(excludeFields, entityDesc);
        HashSet<String> colNames=new HashSet<>();
        Arrays.stream(exculdes).forEach(w->colNames.add(w.getField()));
        Expr where = DaoUtils.getWhereExprFromExample(example, policy, entityDesc,colNames);
        updateWhereExpr(where);
        return this;
    }

    @Override
    public Queryable<T> orderByFields(Function<T, List<Object>> orderBy, boolean isDesc) {
        FieldRefExpr[] executedGetters = DaoUtils.getGetterFields(orderBy, entityDesc);
        for (FieldRefExpr executedGetter : executedGetters) {
            addOrderEntry(new OrderEntryExpr(executedGetter, isDesc));
        }
        return this;
    }

    @Override
    public Queryable<T> orderByAsc(Function<T, List<Object>> orderBy) {
        return orderByFields(orderBy, false);
    }

    @Override
    public Queryable<T> orderByDesc(Function<T, List<Object>> orderBy) {
        return orderByFields(orderBy, true);
    }

    @Override
    public Queryable<T> orderByExpr(Function<T, OrderByExpr> orderByExpr) {
        OrderByExpr orderByExpr1 = DaoUtils.getOrderByExpr(orderByExpr, entityDesc);
        for (OrderEntryExpr orderEntryExpr : orderByExpr1.getOrderEntryExprs()) {
            addOrderEntry(orderEntryExpr);
        }
        return this;
    }

    @Override
    public Queryable<T> orderByMap(Map<String, String> orderByMap) {
        for (Map.Entry<String, String> kv : orderByMap.entrySet()) {
            FieldDesc field = entityDesc.getColumnByColName(kv.getKey());
            if (field == null) {
                field = entityDesc.getColumnByPropName(kv.getKey());
                if (field == null) {
                    throw new IllegalArgumentException("字段不存在:" + kv.getKey());
                }
            }
            boolean asc = StringUtils.strip(kv.getValue()).equalsIgnoreCase("asc");
            addOrderEntry(new OrderEntryExpr($field(field.getColumnName()), !asc));
        }
        return this;
    }

    @Override
    public Queryable<T> orderByMap(Consumer<Map<String, String>> orderByMap) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        orderByMap.accept(map);
        return orderByMap(map);
    }

    @Override
    public Queryable<T> unsafeOrderByMap(Map<String, String> orderByMap) {
        for (Map.Entry<String, String> kv : orderByMap.entrySet()) {
            boolean asc = StringUtils.strip(kv.getValue()).equalsIgnoreCase("asc");
            addOrderEntry(new OrderEntryExpr(new SqlExpr(kv.getKey()), !asc));
        }
        return this;
    }

    @Override
    public Queryable<T> unsafeOrderByMap(Consumer<Map<String, String>> orderByMap) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        orderByMap.accept(map);
        return unsafeOrderByMap(map);
    }


    @Override
    public Queryable<T> select(Function<T, List<Object>> fields) {
        FieldRefExpr[] executedGetters = DaoUtils.getGetterFields(fields, entityDesc);
        for (FieldRefExpr executedGetter : executedGetters) {
            updateSelectEntrys(executedGetter);
        }
        return this;
    }

    @Override
    public Queryable<T> selectOneField(Function<T, Object> field) {
        FieldRefExpr[] executedGetters = DaoUtils.getGetterFields(field, entityDesc);
        for (FieldRefExpr executedGetter : executedGetters) {
            updateSelectEntrys(executedGetter);
        }
        return this;
    }


    @Override
    public Queryable<T> selectExclude(Function<T, List<Object>> fields) {
        FieldRefExpr[] executedGetters = DaoUtils.getGetterFields(fields, entityDesc);
        for (FieldDesc column : entityDesc.getColumns()) {
            boolean exclude = false;
            for (FieldRefExpr executedGetter : executedGetters) {
                if (executedGetter.getField().equals(column.getColumnName())) {
                    exclude = true;
                    break;
                }
            }
            if (exclude) {
                continue;
            }
            updateSelectEntrys($field(column.getColumnName()));
        }
        return this;
    }

    @Override
    public Queryable<T> selectDistinct(Function<T, List<Object>> fields) {
        selectDistinct = true;
        return select(fields);
    }

//    public <T1> GroupByQueryable groupBy(Function<T, List<Object>> fields) {
//        ICardinality card = new HyperLogLogPlus(17);
//        for (int i : new int[] { 1, 2, 3, 2, 4, 3 }) {
//            card.offer(i);
//        }
//        System.out.println(card.cardinality()); // 4
//
//        return null;
//    }


    @SuppressWarnings("unchecked")
    private List toTuple(Function func) {
        Consumer<T> consumer = func::apply;
        FieldRefExpr[] fields = DaoUtils.getGetterFields(consumer, entityDesc);
        QueryExpr e = getQueryExpr();
        QueryExpr queryExpr = $($select(fields), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), e.getLimited());
        List<T> ts = getSqlDriver(SqlCmdType.QUERY).queryForEntity(queryExpr, entityDesc.getRowMapper());
        List resultList = new ArrayList();
        for (T t : ts) {
            Object apply = func.apply(t);
            resultList.add(apply);
        }
        return resultList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1> List<Tuple1<T1>> toTuple1(Function<T, Tuple1<T1>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1, T2> List<Tuple2<T1, T2>> toTuple2(Function<T, Tuple2<T1, T2>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1, T2, T3> List<Tuple3<T1, T2, T3>> toTuple3(Function<T, Tuple3<T1, T2, T3>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1, T2, T3, T4> List<Tuple4<T1, T2, T3, T4>> toTuple4(Function<T, Tuple4<T1, T2, T3, T4>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1, T2, T3, T4, T5> List<Tuple5<T1, T2, T3, T4, T5>> toTuple5(Function<T, Tuple5<T1, T2, T3, T4, T5>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1, T2, T3, T4, T5, T6> List<Tuple6<T1, T2, T3, T4, T5, T6>> toTuple6(Function<T, Tuple6<T1, T2, T3, T4, T5, T6>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T1, T2, T3, T4, T5, T6, T7> List<Tuple7<T1, T2, T3, T4, T5, T6, T7>> toTuple7(Function<T, Tuple7<T1, T2, T3, T4, T5, T6, T7>> tupleFunc) {
        return toTuple(tupleFunc);
    }

    @Override
    public Long count() {
        QueryExpr e = getQueryExpr();
        QueryExpr queryExpr = $($select(ExprFactory.$count()), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), e.getLimited());
        return getSqlDriver(SqlCmdType.QUERY).queryForObject(queryExpr, Long.class);
    }

    @Override
    public Long countDistinct(Function<T, List<Object>> func) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(func, entityDesc);
        QueryExpr e = getQueryExpr();
        QueryExpr queryExpr = $($select($countDistinct(fields)), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), e.getLimited());
        return getSqlDriver(SqlCmdType.QUERY).queryForObject(queryExpr, Long.class);
    }

    private List<T> doDistinct(Function<T, ?> func) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(func, entityDesc);
        QueryExpr e = getQueryExpr();
        QueryExpr queryExpr = $($selectDistinct(fields), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), e.getLimited());
        return getSqlDriver(SqlCmdType.QUERY).queryForEntity(queryExpr, entityDesc.getRowMapper());
    }

    @Override
    public List<T> distinct(Function<T, List<Object>> func) {
      return doDistinct(func);
    }

    private BigDecimal samm(Function<T, Tuple1<Object>> func, StandardMethod method) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(func, entityDesc);
        QueryExpr e = getQueryExpr();
        Expr expr = null;
        switch (method) {
            case Min:
                expr = ExprBuilder.min(fields[0]);
                break;
            case Max:
                expr = ExprBuilder.max(fields[0]);
                break;
            case Sum:
                expr = ExprBuilder.sum(fields[0]);
                break;
            case Avg:
                expr = ExprBuilder.avg(fields[0]);
                break;
        }
        QueryExpr queryExpr = $(ExprFactory.$select(expr), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), e.getLimited());
        return getSqlDriver(SqlCmdType.QUERY).queryForObject(queryExpr, BigDecimal.class);
    }

    @Override
    public BigDecimal sum(Function<T, Tuple1<Object>> func) {
        return samm(func, StandardMethod.Sum);
    }

    @Override
    public BigDecimal avg(Function<T, Tuple1<Object>> func) {
        return samm(func, StandardMethod.Avg);
    }

    @Override
    public BigDecimal min(Function<T, Tuple1<Object>> func) {
        return samm(func, StandardMethod.Min);
    }

    @Override
    public BigDecimal max(Function<T, Tuple1<Object>> func) {
        return samm(func, StandardMethod.Max);
    }

    @Override
    public boolean exists() {
        QueryExpr e = getQueryExpr();
        QueryExpr queryExpr = $($select(ExprFactory.$count()), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), new Limited(1));
        Long aLong = getSqlDriver(SqlCmdType.QUERY).queryForObject(queryExpr, Long.class);
        return aLong != null && aLong >= 1L;
    }

    @Override
    public T first() {
        List<T> ls = top(1);
        if (ls == null || ls.size() < 1) {
            return null;
        }
        return ls.get(0);
    }

    @Override
    public Optional<T> firstOptional() {
        List<T> ls = top(1);
        if (ls == null || ls.size() < 1) {
            return Optional.empty();
        }
        return Optional.of(ls.get(0));
    }

    @Override
    public <TField> TField firstSingleField(Function<T, TField> field) {
        List<TField> tFields = toSingleFieldList(field, 1);
        if(tFields.size()<1) return null;
        return tFields.get(0);
    }

    @Override
    public <TField> Optional<TField> firstSingleFieldOptional(Function<T, TField> field) {
        List<TField> tFields = toSingleFieldList(field, 1);
        if(tFields.size()<1) return Optional.empty();
        return Optional.of(tFields.get(0));
    }

    @Override
    public Traced<T> firstForUpdate() {
        T first = first();
        if(first==null) return null;
        return Traced.startTrace(first);
    }

    @Override
    public List<T> top(int limit) {
        QueryExpr e = getQueryExpr();
        QueryExpr queryExpr = $(e.getSelect(), e.getFrom(), e.getWhere(), e.getOrderBy(), e.getPagination(), new Limited(limit));
        List<T> ls = getSqlDriver(SqlCmdType.QUERY).queryForEntity(queryExpr, entityDesc.getRowMapper());
        return ls;
    }


    @Override
    public List<T> toList() {
        List<T> ls = getSqlDriver(SqlCmdType.QUERY).queryForEntity(getQueryExpr(), entityDesc.getRowMapper());
        return ls;
    }

    @Override
    public <Field1,Field2> Map<Field1,Field2> toDistinctMap(Function<T, Tuple2<Field1, Field2>> func) {
        List<T> list =   doDistinct(func);
        if(list==null || list.size()<1) return Collections.emptyMap();
        Map<Field1,Field2> map=new HashMap<>();
        for (T t : list) {
            Tuple2<Field1, Field2> tuple2 = func.apply(t);
            map.put(tuple2.f1,tuple2.f2);
        }
        return map;
    }

    @Override
    public Stream<T> toStream() {
        return toList().stream();
    }

    @Override
    public List<T> toList(int limit) {
        QueryExpr e = getQueryExpr();
        QueryExpr listExpr = $(e.getSelect(), e.getFrom(), e.getWhere(), e.getOrderBy(), null, new Limited(limit));
        List<T> ls = getSqlDriver(SqlCmdType.QUERY).queryForEntity(listExpr, entityDesc.getRowMapper());
        return ls;
    }

    @Override
    public <TField> List<TField> toSingleFieldList(Function<T, TField> field) {
        return toSingleFieldList(field,null);
    }

    @Override
    public <TField> List<TField> toSingleFieldList(Function<T, TField> field, Integer limit) {
        FieldRefExpr[] fields = DaoUtils.getGetterFields(field, entityDesc, 1);
        QueryExpr e = getQueryExpr();
        QueryExpr listExpr = $($select(fields[0]), e.getFrom(), e.getWhere(), e.getOrderBy(), null, limit==null?null:new Limited(limit));
        List<T> ls = getSqlDriver(SqlCmdType.QUERY).queryForEntity(listExpr, entityDesc.getRowMapper());
        if(ls==null||ls.size()<1) return Collections.emptyList();
        return ls.stream().map(field::apply).collect(Collectors.toList());
    }

    @Override
    public List<Traced<T>> toListForUpdate(int limit) {
        return toList(limit).stream().map(Traced::startTrace).collect(Collectors.toList());
    }


    public <TRet> GroupedQueryable<T,TRet> grouped(Class<TRet> resultClass){
        return new Grouped<>(resultClass);
    }



    @Override
    public Paged<T> toPage(int pageIndex, int pageSize) {
        QueryExpr e = getQueryExpr();
        QueryExpr countExpr = $($select(ExprFactory.$count()), e.getFrom(), e.getWhere(), null, null, null);
        QueryExpr listExpr = $(e.getSelect(), e.getFrom(), e.getWhere(), e.getOrderBy(), new Pagination(pageIndex, pageSize), null);
        Long rows = getSqlDriver(SqlCmdType.QUERY).queryForObject(countExpr, Long.class);
        List<T> ls = getSqlDriver(SqlCmdType.QUERY).queryForEntity(listExpr, entityDesc.getRowMapper());
        return Paged.create(pageIndex, pageSize, rows.intValue(), ls);
    }

    @Override
    public List<T> toPageCountLess(int pageIndex, int pageSize) {
        QueryExpr e = getQueryExpr();
        QueryExpr listExpr = $(e.getSelect(), e.getFrom(), e.getWhere(), e.getOrderBy(), new Pagination(pageIndex, pageSize), null);
        List<T> ls = getSqlDriver(SqlCmdType.QUERY).queryForEntity(listExpr, entityDesc.getRowMapper());
        return ls;
    }

    @Override
    public Paged<T> toPageHLLP(int pageIndex, int pageSize, Function<T, String> idFunc) {
        List<T> ts = toPageCountLess(pageIndex, pageSize);
        long estimate = HLLPUtils.estimate(ts, idFunc);
        return Paged.create(pageIndex,pageSize,(int)estimate,ts);
    }

    @Override
    public QueryExpr getQueryExpr() {

        //默认是Select all
        SelectExpr selectExpr = null;
        if (selectExprEntrys == null || selectExprEntrys.size() < 1) {
            SelectExpr allExpr = DaoUtils.getSelectAllExpr(entityDesc);
            if (selectDistinct) {
                selectExpr = new SelectExpr(allExpr.getFields(), true);
            } else {
                selectExpr = allExpr;
            }
        } else {
            if (selectDistinct) {
                selectExpr = ExprFactory.$selectDistinct(selectExprEntrys);
            } else {
                selectExpr = ExprFactory.$select(selectExprEntrys);
            }
        }

        if (whereExpr == null) {
            throw new IllegalArgumentException("where条件不能为空");
        }
        WhereExpr where = ExprFactory.$where(whereExpr);

        OrderByExpr orderByExpr = null;
        if (orderEntryExprs != null && orderEntryExprs.size() > 0) {
            orderByExpr = ExprFactory.$orderBy(orderEntryExprs);
        }

        QueryExpr expr = ExprFactory.$query(selectExpr, $from(entityDesc.getTableName()), where, orderByExpr, pagination, limited);
        return expr;
    }

    private SqlDriver getSqlDriver(SqlCmdType sqlCmdType){
        return sqlDriverHolder.getSqlDriver(entityDesc,sqlCmdType);
    }

    private class Grouped<T extends Entity,TRet> implements GroupedQueryable<T,TRet> {

        private Class<TRet> retClass;
        private List<Tuple2<StandardMethod,BiConsumer<T,TRet>>> groups=new ArrayList<>();
        private List<SelectEntryExpr> selectEntryExprs=new ArrayList<>();

        public Grouped(Class<TRet> retClass){
            this.retClass=retClass;
        }

        private void addGroupBy(StandardMethod method,BiConsumer<T,TRet> func){
            if(func==null) {
                return;
            }
            groups.add(Tuple.of(method,func));
        }

        public GroupedQueryable<T,TRet> groupBy(Function<T,List<Object>> func){
            return this;
        }


        public GroupedQueryable<T,TRet> selectSum(BiConsumer<T,TRet> func){
            addGroupBy(StandardMethod.Sum,func);

            return this;
        }
        public  GroupedQueryable<T,TRet> selectCountField(BiConsumer<T,TRet> func){
            addGroupBy(StandardMethod.Count,func);
            return this;
        }
        public  GroupedQueryable<T,TRet> selectMax(BiConsumer<T,TRet> func){
            addGroupBy(StandardMethod.Count,func);
            return this;
        }
        @SneakyThrows
        public  GroupedQueryable<T,TRet> selectMin(BiConsumer<T,TRet> func){
            addGroupBy(StandardMethod.Count,func);

            TRet retProxy = DaoUtils.proxy(retClass);
            TRet retRaw = retClass.newInstance();
            T entityProxy = (T) DaoUtils.proxy(entityDesc.getEntityClass());
            T entityRaw = (T) entityDesc.getEntityClass().newInstance();

            func.accept(entityProxy,retRaw);

            FieldRefExpr[] selectFields = DaoUtils.getGetterFields(entityProxy, entityDesc);

            func.accept(entityRaw,retProxy);

            List<PropertyCall> setterList = DaoUtils.getExecutedSetterList(entityProxy);

            if(selectFields.length!=setterList.size()){
                throw new RuntimeException("非法操作，一个setter对应一个getter");
            }

            return this;
        }
        public  GroupedQueryable<T,TRet> selectCount(BiConsumer<Long,TRet> func){
//            addGroupBy(StandardMethod.Count,func);
            return this;
        }
        public  GroupedQueryable<T,TRet> selectCountDistinct(BiConsumer<T,TRet> func){
            addGroupBy(StandardMethod.Count,func);
            return this;
        }
        public  GroupedQueryable<T,TRet> selectSumDistinct(BiConsumer<T,TRet> func){
            addGroupBy(StandardMethod.Count,func);
            return this;
        }
        public GroupedQueryable<T,TRet> having(Function<T,Expr> groupBy){
            return this;
        }
        public List<TRet> toList(){
            return null;
        }

    }
}
