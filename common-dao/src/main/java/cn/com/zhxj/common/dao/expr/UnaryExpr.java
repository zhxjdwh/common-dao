package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 一元表达式
 */
@Getter
public class UnaryExpr extends Expr {

    /**
     * 操作元素
     */
    private final Expr expr;

    public UnaryExpr(Expr expr, ExprType.UnaryType type) {
        super(type);
        this.expr=expr;
    }

}
