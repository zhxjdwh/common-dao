package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 逻辑表达式
 */
@Getter
public class LogicalExpr extends BinaryExpr {
    public LogicalExpr(Expr left,ExprType type,Expr right){
        super(left,type,right);
    }
}
