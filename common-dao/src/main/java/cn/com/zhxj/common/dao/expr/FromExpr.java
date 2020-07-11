package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

@Getter
public class FromExpr extends Expr {

    private Expr expr;

    public FromExpr(Expr expr) {
        super(ExprType.From);
        this.expr=expr;
    }
}
