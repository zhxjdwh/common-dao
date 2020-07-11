package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * Table表达式
 */
@Getter
public class TableRefExpr extends Expr {

    private String table;

    public TableRefExpr(String table) {
        super(ExprType.TableRef);
        this.table=table;
    }
}
