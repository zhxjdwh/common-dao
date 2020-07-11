package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 二元表达式
 */
@Getter
public class BinaryExpr extends Expr {

    protected final Expr left;
    protected final Expr right;

    public BinaryExpr(Expr left,ExprType type,Expr right) {
        super(type);
        this.left=left;
        this.right=right;
    }


}
