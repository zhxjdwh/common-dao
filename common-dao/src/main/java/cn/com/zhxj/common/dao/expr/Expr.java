package cn.com.zhxj.common.dao.expr;

import cn.com.zhxj.common.dao.core.ExprAliases;
import lombok.Getter;

/**
 * 表达式
 */
@Getter
public abstract class Expr {
    protected final ExprType type;

    /**
     * 将一个表达式 关联到一个对象上
     * @param aliasObj
     * @param <T>
     * @return
     */
    public <T> T aliasTo(T aliasObj){
        ExprAliases.addAlias(this,aliasObj);
        return aliasObj;
    }

    public Expr(ExprType type) {
        this.type = type;
    }

}
