package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 字段表达式
 */
@Getter
public class ConstantExpr extends Expr {

    private Object data;

    public ConstantExpr(Object data) {
        super(ExprType.Constant);
        this.data=data;
    }
}
