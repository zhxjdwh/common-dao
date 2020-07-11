package cn.com.zhxj.common.dao.expr;

import cn.com.zhxj.common.dao.core.Limited;
import cn.com.zhxj.common.dao.core.Pagination;
import lombok.Getter;

/**
 * Query表达式
 */
@Getter
public class QueryExpr extends Expr {

    private final FromExpr    from;
    private final WhereExpr   where;
    private final OrderByExpr orderBy;
    private final SelectExpr  select;
    private final Limited     limited;
    private final Pagination  pagination;


    public QueryExpr(FromExpr from, WhereExpr where, OrderByExpr orderBy, SelectExpr select, Pagination pagination, Limited limited) {
        super(ExprType.Query);
        this.from = from;
        this.select = select;
        this.where = where;
        this.orderBy = orderBy;
        this.limited=limited;
        this.pagination = pagination;
    }


}
