package cn.com.zhxj.common.dao.dialect;

import cn.com.zhxj.common.dao.expr.Expr;
import cn.com.zhxj.common.dao.expr.FieldRefExpr;

public class WhereSafeVisitor extends ExprVisitor {

    private boolean safe=false;

    @Override
    public Expr visit(Expr expr) {
        if(safe) return expr;
        return super.visit(expr);
    }

    @Override
    protected Expr visitFieldRef(FieldRefExpr expr) {
        safe=true;
        return expr;
    }


    /**
     * 只要where条件里面包含了字段引用 那就认为是安全的
     * @return
     */
    public boolean isSafe(){
        return safe;
    }
}
