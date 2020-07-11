package cn.com.zhxj.common.dao.core;


import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.ExprType.BinaryType;
import cn.com.zhxj.common.dao.expr.ExprType.LogicalType;
import cn.com.zhxj.common.dao.mapping.EntityMappingFactory;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.util.DaoUtils;

import java.util.List;

import static cn.com.zhxj.common.dao.util.Asserts.assertFieldNotNull;

public class ExprFactory {


    public static ConstantExpr $const(Object val) {
        return new ConstantExpr(val);
    }

    public static ConstantExpr $const(List val) {
        return new ConstantExpr(val);
    }


    public static QueryExpr $query(SelectExpr selectExpr, FromExpr from, WhereExpr whereExpr, OrderByExpr orderByExpr, Pagination pagination, Limited limited) {
        return new QueryExpr(from, whereExpr, orderByExpr, selectExpr, pagination, limited);
    }



    //<editor-fold desc="Field 表达式">
    public static <T extends Entity> FieldRefExpr $field(String field) {
        return new FieldRefExpr(field);
    }

    public static FieldRefExpr $field(FieldDesc fieldDesc) {
        return new FieldRefExpr(fieldDesc.getColumnName());
    }

    //</editor-fold>


    //<editor-fold desc="Select">

    public static <T extends Entity> SelectExpr $select(List<Expr> fields) {
        Expr[] fieldRefExprs = new FieldRefExpr[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            fieldRefExprs[i] = fields.get(i);
        }
        return new SelectExpr(fieldRefExprs);
    }

    public static <T extends Entity> SelectExpr $selectDistinct(List<Expr> fields) {
        Expr[] fieldRefExprs = new FieldRefExpr[fields.size()];
        for (int i = 0; i < fields.size(); i++) {
            fieldRefExprs[i] = fields.get(i);
        }
        return new SelectExpr(fieldRefExprs, true);
    }

    public static <T extends Entity> SelectExpr $selectDistinct(Expr... fields) {
        Expr[] fieldRefExprs = new FieldRefExpr[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldRefExprs[i] = fields[i];
        }
        return new SelectExpr(fieldRefExprs, true);
    }

    public static <T extends Entity> SelectExpr $select(Expr... fields) {
        return new SelectExpr(fields);
    }

    //</editor-fold>

    //<editor-fold desc="From表达式">
    public static <T extends Entity> FromExpr $from(TableRefExpr tableRefExpr) {
        return new FromExpr(tableRefExpr);
    }

    public static <T extends Entity> FromExpr $from(String tableName) {
        return new FromExpr(new TableRefExpr(tableName));
    }

    public static <T extends Entity> FromExpr $from(Class<T> entityCls) {
        String tableName = EntityMappingFactory.getTableName(entityCls);
        return new FromExpr(new TableRefExpr(tableName));
    }
    //</editor-fold>

    //<editor-fold desc="Table表达式">
    public static TableRefExpr $table(String tableName) {
        return new TableRefExpr(tableName);
    }
    //</editor-fold>

    public static UpdateExpr $update(TableRefExpr table, List<UpdateExpr.Entry> entries, WhereExpr whereExpr) {
        return new UpdateExpr(table, entries, whereExpr);
    }

    public static DeleteExpr $delete(TableRefExpr tableRefExpr, WhereExpr whereExpr) {
        return new DeleteExpr(tableRefExpr, whereExpr);
    }

    public static QueryExpr $(SelectExpr selectExpr, FromExpr from, WhereExpr whereExpr, OrderByExpr orderByExpr, Pagination pagination, Limited limited) {
        return new QueryExpr(from, whereExpr, orderByExpr, selectExpr, pagination, limited);
    }


    //<editor-fold desc="Where构造">
    public static <T extends Entity> WhereExpr $where(Expr expr) {
        return new WhereExpr(expr);
    }

    //</editor-fold>

    //<editor-fold desc="逻辑表达式构造">

    public static <T extends Entity> LogicalExpr $logical(Expr left, LogicalType type, Expr right) {
        return new LogicalExpr(left, type, right);
    }
    //</editor-fold>


    //<editor-fold desc="Order By">

    public static OrderByExpr $orderBy(OrderEntryExpr... orderEntryExprs) {
        return new OrderByExpr(orderEntryExprs);
    }

    public static OrderByExpr $orderBy(List<OrderEntryExpr> orderEntryExprs) {
        return new OrderByExpr(orderEntryExprs.toArray(new OrderEntryExpr[0]));
    }

    //</editor-fold>



    //<editor-fold desc="二元表达式构造">

    public static <T extends Entity> BinaryExpr $binary(Expr left, BinaryType type, Expr right) {
        return new BinaryExpr(left, type, right);
    }
    //</editor-fold>


    public static <T extends Entity> SqlExpr $sql(String sql) {
        return new SqlExpr(sql);
    }



    //<editor-fold desc="一元表达式">
    //一元表达式

    //</editor-fold>

    //<editor-fold desc="SQL 常用 函数">

    public static MethodCallExpr $count() {
        return new MethodCallExpr(null, StandardMethod.Count, null);
    }

    public static MethodCallExpr $countDistinct(Expr... exprs) {
        return new MethodCallExpr(null, StandardMethod.CountDistinct, exprs);
    }

    public static MethodCallExpr $lower(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Lower, new Expr[]{expr});
    }

    public static MethodCallExpr $upper(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Upper, new Expr[]{expr});
    }

    public static MethodCallExpr $abs(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Abs, new Expr[]{expr});
    }

    public static MethodCallExpr $len(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Len, new Expr[]{expr});
    }

    public static MethodCallExpr $avg(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Avg, new Expr[]{expr});
    }

    public static MethodCallExpr $sum(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Sum, new Expr[]{expr});
    }

    public static MethodCallExpr $min(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Min, new Expr[]{expr});
    }

    public static MethodCallExpr $max(Expr expr) {
        return new MethodCallExpr(null, StandardMethod.Max, new Expr[]{expr});
    }

    public static MethodCallExpr $upper(String field) {
        FieldRefExpr fieldRefExpr = assertFieldNotNull(DaoUtils.getNextField(false));
        return new MethodCallExpr(null, StandardMethod.Upper, new Expr[]{fieldRefExpr});
    }

    public static MethodCallExpr $lower(String field) {
        FieldRefExpr fieldRefExpr = assertFieldNotNull(DaoUtils.getNextField(false));
        return new MethodCallExpr(null, StandardMethod.Lower, new Expr[]{fieldRefExpr});
    }


    /**
     * unit timestamp  oracle/mysql/mssql的 timestamp 类型
     *
     * @return
     */
    public static MethodCallExpr $currentTimestamp() {
        return new MethodCallExpr(null, StandardMethod.CurrentTimestamp, null);
    }

    /**
     * DateTime  oracle/mysql/mssql的 DateTime 类型
     *
     * @return
     */
    public static MethodCallExpr $currentDateTime() {
        return new MethodCallExpr(null, StandardMethod.CurrentDateTime, null);
    }

    //</editor-fold>


    //<editor-fold desc="工具方法">

    private static Expr getExpr(Object field){
        Expr expr=null;
        if (field instanceof Expr) {
            expr = (Expr) field;
        }
        expr = assertFieldNotNull(DaoUtils.getNextField(false));
        return expr;
    }


    //</editor-fold>

}
