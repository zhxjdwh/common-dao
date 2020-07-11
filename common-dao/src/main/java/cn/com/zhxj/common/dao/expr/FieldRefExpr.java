package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 字段表达式
 */
@Getter
public class FieldRefExpr extends Expr {

    private String field;

    public FieldRefExpr(String field) {
        super(ExprType.FieldRef);
        this.field=field;
    }
}
