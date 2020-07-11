package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * Where表达式
 */
@Getter
public class WhereExpr extends Expr {

    private Expr expr;

    public WhereExpr(Expr expr) {
        super(ExprType.Where);
        this.expr=expr;
    }
}
