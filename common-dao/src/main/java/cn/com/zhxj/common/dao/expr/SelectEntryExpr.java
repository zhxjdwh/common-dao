package cn.com.zhxj.common.dao.expr;

import lombok.Getter;

/**
 * Select表达式
 */
@Getter
public class SelectEntryExpr extends Expr {

    private Expr expr;
    private String alias;

    public SelectEntryExpr(Expr expr,String alias) {
        super(ExprType.SelectEntry);
        this.expr=expr;
        this.alias=alias;
    }
    public SelectEntryExpr(Expr expr) {
        super(ExprType.SelectEntry);
        this.expr=expr;
        this.alias=null;
    }


}
