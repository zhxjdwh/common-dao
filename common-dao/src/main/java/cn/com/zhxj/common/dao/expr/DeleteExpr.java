package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * Delete表达式
 */
@Getter
public class DeleteExpr extends Expr {

    private final TableRefExpr table;
    private final WhereExpr    where;

    public DeleteExpr(TableRefExpr table, WhereExpr whereExpr) {
        super(ExprType.Delete);
        this.table = table;
        this.where = whereExpr;
    }


}
