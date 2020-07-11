package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 排序表达式
 */
@Getter
public class OrderEntryExpr extends Expr {

    private Expr expr;
    private boolean isDesc;

    public OrderEntryExpr(Expr expr, boolean isDesc ){
        super(ExprType.OrderEntryType.Asc);
        this.expr=expr;
        this.isDesc=isDesc;
    }
}
