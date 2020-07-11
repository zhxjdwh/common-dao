package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * Select表达式
 */
@Getter
public class SelectExpr extends Expr {

    private Expr[] fields;
    private boolean distinct;

    public SelectExpr(Expr[] fields) {
        super(ExprType.Select);
        this.fields=fields;
        this.distinct=false;
    }

    public SelectExpr(Expr[] fields,boolean distinct) {
        super(ExprType.Select);
        this.fields=fields;
        this.distinct=distinct;
    }
}
