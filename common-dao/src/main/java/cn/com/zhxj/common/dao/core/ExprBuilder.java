package cn.com.zhxj.common.dao.core;


import cn.com.zhxj.common.dao.Queryable;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.ExprType.BinaryType;
import cn.com.zhxj.common.dao.expr.ExprType.LogicalType;
import cn.com.zhxj.common.dao.expr.ExprType.OrderEntryType;
import cn.com.zhxj.common.dao.expr.ExprType.UnaryType;
import cn.com.zhxj.common.dao.util.DaoUtils;
import cn.com.zhxj.common.dao.util.ReflectionUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static cn.com.zhxj.common.dao.core.ExprFactory.$const;
import static cn.com.zhxj.common.dao.core.ExprFactory.$query;
import static cn.com.zhxj.common.dao.util.Asserts.assertFieldNotNull;

public class ExprBuilder {


    public static final LogicalType and = LogicalType.And;
    public static final LogicalType or = LogicalType.Or;
    public static final BinaryType gt         = BinaryType.Gt;
    public static final BinaryType gte        = BinaryType.Gte;
    public static final BinaryType lt         = BinaryType.Lt;
    public static final BinaryType lte        = BinaryType.Lte;
    public static final BinaryType eq         = BinaryType.Eq;
    public static final BinaryType notEq      = BinaryType.NotEq;
    public static final BinaryType like       = BinaryType.Like;
    public static final BinaryType startWith  = BinaryType.StartWith;
    public static final BinaryType endWith    = BinaryType.EndWith;
    public static final BinaryType contain    = BinaryType.Contain;
    public static final BinaryType notLike    = BinaryType.NotLike;
    public static final BinaryType between    = BinaryType.Between;
    public static final BinaryType notBetween = BinaryType.NotBetween;
    public static final BinaryType in         = BinaryType.In;
    public static final BinaryType notIn      = BinaryType.NotIn;
    public static final BinaryType add        = BinaryType.Add;
    public static final BinaryType sub        = BinaryType.Sub;
    public static final BinaryType mul        = BinaryType.Mul;
    public static final BinaryType div        = BinaryType.Div;

//        public static final BinaryType Mod        = new BinaryType(ExprTypeName.Mod);

    public static LogicalExpr $(Expr e1, LogicalType e2, Expr e3) {
        return new LogicalExpr(e1, e2, e3);
    }
    public static LogicalExpr $(Object e1, LogicalType e2, Expr e3) {
        return new LogicalExpr(getExpr(e1), e2, e3);
    }
    public static LogicalExpr $(Expr e1, LogicalType e2, Object e3) {
        return new LogicalExpr(e1, e2, getExpr(e3));
    }


    //<editor-fold desc="逻辑表达式构造">

    public static LogicalExpr $(Expr e1, ExprType.LogicalType e2, Expr e3, LogicalType e4, Expr e5) {
        return $($(e1, e2, e3), e4, e5);
    }


    //</editor-fold>


    //<editor-fold desc="Order By">

    public static <TField1> OrderByExpr $(TField1 field1, OrderEntryType orderByType1) {
        OrderEntryExpr orderEntry1 = createOrderEntry(field1, orderByType1);
        OrderEntryExpr[] orderEntryExprs = {
                orderEntry1
        };
        return new OrderByExpr(orderEntryExprs);
    }

    public static <TField1> OrderByExpr $(TField1 field1, OrderEntryType orderByType1, TField1 field2, OrderEntryType orderByType2) {
        OrderEntryExpr orderEntry1 = createOrderEntry(field1, orderByType1);
        OrderEntryExpr orderEntry2 = createOrderEntry(field2, orderByType2);
        OrderEntryExpr[] orderEntryExprs = {
                orderEntry1,
                orderEntry2
        };
        return new OrderByExpr(orderEntryExprs);
    }

    public static <TField1> OrderByExpr $(TField1 field1, OrderEntryType orderByType1, TField1 field2, OrderEntryType orderByType2, TField1 field3, OrderEntryType orderByType3) {
        OrderEntryExpr orderEntry1 = createOrderEntry(field1, orderByType1);
        OrderEntryExpr orderEntry2 = createOrderEntry(field2, orderByType2);
        OrderEntryExpr orderEntry3 = createOrderEntry(field3, orderByType3);
        OrderEntryExpr[] orderEntryExprs = {
                orderEntry1,
                orderEntry2,
                orderEntry3
        };
        return new OrderByExpr(orderEntryExprs);
    }

    public static <TField1> OrderByExpr $(TField1 field1, OrderEntryType orderByType1, TField1 field2, OrderEntryType orderByType2, TField1 field3, OrderEntryType orderByType3, TField1 field4, OrderEntryType orderByType4) {
        OrderEntryExpr orderEntry1 = createOrderEntry(field1, orderByType1);
        OrderEntryExpr orderEntry2 = createOrderEntry(field2, orderByType2);
        OrderEntryExpr orderEntry3 = createOrderEntry(field3, orderByType3);
        OrderEntryExpr orderEntry4 = createOrderEntry(field4, orderByType4);

        OrderEntryExpr[] orderEntryExprs = {
                orderEntry1,
                orderEntry2,
                orderEntry3,
                orderEntry4
        };
        return new OrderByExpr(orderEntryExprs);
    }

    private static OrderEntryExpr createOrderEntry(Object field, OrderEntryType orderByType) {
        if (field instanceof Expr) {
            return new OrderEntryExpr((Expr) field, orderByType == OrderEntryType.Desc);
        }
        FieldRefExpr fieldRefExpr = assertFieldNotNull(DaoUtils.getNextField(false));
        return new OrderEntryExpr(fieldRefExpr, orderByType == OrderEntryType.Desc);
    }

    public static <T extends Entity> OrderByExpr $(OrderEntryExpr... orderEntryExprs) {
        return new OrderByExpr(orderEntryExprs);
    }

    //</editor-fold>

    //<editor-fold desc=" List/Tuple ">
    public static List<Object> list(Object... fields) {
        return Arrays.asList(fields);
    }

    @SuppressWarnings("unchecked")
    public static List<Integer> list(int[] fields) {
        return (List<Integer>) ReflectionUtils.primitiveArrayToList(fields);
    }

    @SuppressWarnings("unchecked")
    public static List<Long> list(long[] fields) {
        return (List<Long>) ReflectionUtils.primitiveArrayToList(fields);
    }

    @SuppressWarnings("unchecked")
    public static List<Double> list(double[] fields) {
        return (List<Double>) ReflectionUtils.primitiveArrayToList(fields);
    }

    @SuppressWarnings("unchecked")
    public static List<Float> list(float[] fields) {
        return (List<Float>) ReflectionUtils.primitiveArrayToList(fields);
    }

    @SuppressWarnings("unchecked")
    public static List<Byte> list(byte[] fields) {
        return (List<Byte>) ReflectionUtils.primitiveArrayToList(fields);
    }

    public static <T1> Tuple1<T1> tuple(T1 t1) {
        return Tuple.of(t1);
    }

    public static <T1, T2> Tuple2<T1, T2> tuple(T1 t1, T2 t2) {
        return Tuple.of(t1, t2);
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> tuple(T1 t1, T2 t2, T3 t3) {
        return Tuple.of(t1, t2, t3);
    }

    public static <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4> tuple(T1 t1, T2 t2, T3 t3, T4 t4) {
        return Tuple.of(t1, t2, t3, t4);
    }

    public static <T1, T2, T3, T4, T5> Tuple5<T1, T2, T3, T4, T5> tuple(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5) {
        return Tuple.of(t1, t2, t3, t4, t5);
    }

    public static <T1, T2, T3, T4, T5, T6> Tuple6<T1, T2, T3, T4, T5, T6> tuple(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6) {
        return Tuple.of(t1, t2, t3, t4, t5, t6);
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Tuple7<T1, T2, T3, T4, T5, T6, T7> tuple(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7) {
        return Tuple.of(t1, t2, t3, t4, t5, t6, t7);
    }
    //</editor-fold>


    //<editor-fold desc="二元表达式构造">

    public static BinaryExpr $(Expr exprLeft, BinaryType e2, Expr exprRight) {
        return new BinaryExpr(exprLeft, e2, exprRight);
    }

    public static BinaryExpr $(Object field, BinaryType e2, Expr exprRight) {
        return new BinaryExpr(getExpr(field), e2, exprRight);
    }

    public static BinaryExpr $(Expr expr, BinaryType e2, Object obj) {
        return new BinaryExpr(expr, e2, $const(obj));
    }

    public static BinaryExpr $(Expr expr, BinaryType e2, List objs) {
        return new BinaryExpr(expr, e2, $const(objs));
    }

    public static BinaryExpr $(Integer field, BinaryType e2, Integer[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(Long field, BinaryType e2, Long[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(Date field, BinaryType e2, Date[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(String field, BinaryType e2, String[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(Float field, BinaryType e2, Float[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(Double field, BinaryType e2, Double[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(Byte field, BinaryType e2, Byte[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static BinaryExpr $(BigDecimal field, BinaryType e2, BigDecimal[] obj) {
        return createBinary(field, e2, Arrays.asList(obj));
    }

    public static <T> BinaryExpr $(T field, BinaryType e2, List<T> obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(Integer field, BinaryType e2, Integer obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(Long field, BinaryType e2, Long obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(Date field, BinaryType e2, Date obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(String field, BinaryType e2, String obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(Float field, BinaryType e2, Float obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(Double field, BinaryType e2, Double obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(BigDecimal field, BinaryType e2, BigDecimal obj) {
        return createBinary(field, e2, obj);
    }

    public static BinaryExpr $(Long field, BinaryType e2, int[] obj) {
        Object list = ReflectionUtils.primitiveArrayToList(obj);
        return createBinary(field, e2, list);
    }

    public static BinaryExpr $(Long field, BinaryType e2, long[] obj) {
        Object list = ReflectionUtils.primitiveArrayToList(obj);
        return createBinary(field, e2, list);
    }

    public static BinaryExpr $(Float field, BinaryType e2, float[] obj) {
        Object list = ReflectionUtils.primitiveArrayToList(obj);
        return createBinary(field, e2, list);
    }

    public static BinaryExpr $(Double field, BinaryType e2, double[] obj) {
        Object list = ReflectionUtils.primitiveArrayToList(obj);
        return createBinary(field, e2, list);
    }

    public static BinaryExpr $(Byte field, BinaryType e2, byte[] obj) {
        Object list = ReflectionUtils.primitiveArrayToList(obj);
        return createBinary(field, e2, list);
    }

    public static BinaryExpr $(Object field, BinaryType e2, Queryable queryable) {
        QueryExpr expr = queryable.getQueryExpr();
        QueryExpr queryExpr = $query(expr.getSelect(), expr.getFrom(), expr.getWhere(), null, null, null);
        return createBinary(field, e2, queryExpr);
    }

    private static BinaryExpr createBinary(Object left, BinaryType type, Object right) {
        return new BinaryExpr(getExpr(left), type, getExpr(right));
    }

    //</editor-fold>


    //<editor-fold desc="一元表达式构造">


    public static <T extends Entity> UnaryExpr $(Object field, UnaryType e2) {
        return new UnaryExpr(getExpr(field), e2);
    }

    public static <T extends Entity> UnaryExpr $(Expr left, UnaryType e2) {
        return new UnaryExpr(left, e2);
    }
    //</editor-fold>


    //<editor-fold desc="逻辑表达式">
    //逻辑表达式

    public static LogicalType and() {
        return LogicalType.And;
    }

    public static LogicalType or() {
        return LogicalType.Or;
    }

    public static BinaryExpr $and(Expr... exprs) {
        if(exprs.length<2) throw new IllegalArgumentException("逻辑表达式至少需要2个子表达式");
        BinaryExpr expr = $(exprs[0],and(),exprs[1]);
        for (int i = 1; i < exprs.length; i++) {
            expr=$(expr,and(),exprs[i]);
        }
        return expr;
    }


    public static BinaryExpr $or(Expr... exprs) {
        if(exprs.length<2) throw new IllegalArgumentException("逻辑表达式至少需要2个子表达式");
        BinaryExpr expr = $(exprs[0],or(),exprs[1]);
        for (int i = 1; i < exprs.length; i++) {
            expr=$(expr,or(),exprs[i]);
        }
        return expr;
    }

    //</editor-fold>


    //<editor-fold desc="二元表达式">
    //二元表达式

    public static BinaryType gt() {
        return BinaryType.Gt;
    }

    public static BinaryType gte() {
        return BinaryType.Gte;
    }

    public static BinaryType lt() {
        return BinaryType.Lt;
    }

    public static BinaryType lte() {
        return BinaryType.Lte;
    }

    public static BinaryType eq() {
        return BinaryType.Eq;
    }

    public static BinaryType notEq() {
        return BinaryType.NotEq;
    }

    public static BinaryType in() {
        return BinaryType.In;
    }

    public static BinaryType notIn() {
        return BinaryType.NotIn;
    }

    public static BinaryType like() {
        return BinaryType.Like;
    }

    public static BinaryType notLike() {
        return BinaryType.NotLike;
    }

    public static BinaryType between() {
        return BinaryType.Between;
    }

    public static BinaryType notBetween() {
        return BinaryType.NotBetween;
    }

    public static BinaryType add() {
        return BinaryType.Add;
    }

    public static BinaryType sub() {
        return BinaryType.Sub;
    }

    public static BinaryType mul() {
        return BinaryType.Mul;
    }

    public static BinaryType div() {
        return BinaryType.Div;
    }

    public static BinaryType startWith() {
        return BinaryType.StartWith;
    }

    public static BinaryType endWith() {
        return BinaryType.EndWith;
    }

    public static BinaryType contain() {
        return BinaryType.Contain;
    }

    //</editor-fold>


    //<editor-fold desc="一元表达式">
    //一元表达式
    public static UnaryType isNull() {
        return UnaryType.IsNull;
    }

    public static UnaryType isNotNull() {
        return UnaryType.IsNotNull;
    }

    public static OrderEntryType asc() {
        return OrderEntryType.Asc;
    }

    public static OrderEntryType desc() {
        return OrderEntryType.Desc;
    }
    //</editor-fold>

    //<editor-fold desc="SQL 常用 函数">

    public static MethodCallExpr count() {
        return new MethodCallExpr(null, StandardMethod.Count, null);
    }

    public static MethodCallExpr countDistinct(Expr... exprs) {
        return new MethodCallExpr(null, StandardMethod.CountDistinct, exprs);
    }

    public static MethodCallExpr lower(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Lower, new Expr[]{expr});
    }

    public static MethodCallExpr upper(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Upper, new Expr[]{expr});
    }

    public static MethodCallExpr abs(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Abs, new Expr[]{expr});
    }

    public static MethodCallExpr len(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Len, new Expr[]{expr});
    }

    public static MethodCallExpr avg(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Avg, new Expr[]{expr});
    }

    public static MethodCallExpr sum(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Sum, new Expr[]{expr});
    }

    public static MethodCallExpr min(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Min, new Expr[]{expr});
    }

    public static MethodCallExpr max(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Max, new Expr[]{expr});
    }
    public static MethodCallExpr upper(String field) {
        Expr expr = getExpr(field);
        return new MethodCallExpr(null, StandardMethod.Upper, new Expr[]{expr});
    }
    public static MethodCallExpr lower(String field) {
        Expr expr = getExpr(field);
        return new MethodCallExpr(null, StandardMethod.Lower, new Expr[]{expr});
    }

    public static MethodCallExpr replace(String field,String replaceStr,String replaceTo) {
        Expr expr = getExpr(field);
        Expr replaceStrExpr = getExpr(replaceStr);
        Expr replaceToExpr = getExpr(replaceTo);
        return new MethodCallExpr(null, StandardMethod.Replace, new Expr[]{expr,replaceStrExpr,replaceToExpr});
    }
    public static MethodCallExpr concat(String... fields) {
        Expr[] exprs = Arrays.stream(fields).map(ExprBuilder::getExpr).toArray(Expr[]::new);
        return new MethodCallExpr(null, StandardMethod.Concat, exprs);
    }

    public static MethodCallExpr call(String methodName,Object... args) {
        if(args==null||args.length<1){
            return new MethodCallExpr(methodName, null, null);
        }
        Expr[] exprs = Arrays.stream(args).map(w -> getExpr(w)).toArray(Expr[]::new);
        return new MethodCallExpr(methodName, null, exprs);
    }

    /**
     * unit timestamp  oracle/mysql/mssql的 timestamp 类型
     *
     * @return
     */
    public static MethodCallExpr currentTimestamp() {
        return new MethodCallExpr(null, StandardMethod.CurrentTimestamp, null);
    }

    /**
     * DateTime  oracle/mysql/mssql的 DateTime 类型
     *
     * @return
     */
    public static MethodCallExpr currentDateTime() {
        return new MethodCallExpr(null, StandardMethod.CurrentDateTime, null);
    }

    /**
     * unit timestamp  oracle/mysql/mssql的 timestamp 类型
     *
     * @return
     */
    public static MethodCallExpr currentTimestampUtc() {
        return new MethodCallExpr(null, StandardMethod.CurrentTimestampUtc, null);
    }

    /**
     * DateTime  oracle/mysql/mssql的 DateTime 类型
     *
     * @return
     */
    public static MethodCallExpr currentDateTimeUtc() {
        return new MethodCallExpr(null, StandardMethod.CurrentDateTimeUtc, null);
    }

    //</editor-fold>


    //<editor-fold desc="工具方法">

    private static Expr getExpr(Object field){
        Expr expr=null;
        if(field==null){
            expr = assertFieldNotNull(DaoUtils.getNextField(false));
        }
        if (expr==null && field instanceof Expr) {
            expr = (Expr) field;
        }
        if(expr==null){
            expr=$const(field);
        }
        return expr;
    }



    //</editor-fold>

}
