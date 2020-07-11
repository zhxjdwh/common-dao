package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * sql表达式
 */
@Getter
public class SqlExpr extends Expr {

    public static final SqlExpr COUNT1=new SqlExpr("COUNT(1)");

    private final String sql;

    public SqlExpr(String sql) {
        super(ExprType.Sql);
        this.sql=sql;
    }
}
