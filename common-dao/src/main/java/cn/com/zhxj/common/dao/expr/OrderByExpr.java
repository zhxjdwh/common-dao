package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * 排序表达式
 */
@Getter
public class OrderByExpr extends Expr {

  private OrderEntryExpr[] orderEntryExprs;

    public OrderByExpr(OrderEntryExpr[] orderEntryExprs){
        super(ExprType.OrderByType.OrderBy);
        this.orderEntryExprs=orderEntryExprs;
    }
}
