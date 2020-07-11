package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * Query表达式
 */
@Getter
public class MethodCallExpr extends Expr {

    private final String         method;
    private final Expr[]         params;
    private final StandardMethod standardMethod;


    public MethodCallExpr(String method, StandardMethod standardMethod, Expr[] params) {
        super(ExprType.MethodCallType.MethodCall);
        this.method = method;
        this.params = params;
        this.standardMethod = standardMethod;
    }

}
