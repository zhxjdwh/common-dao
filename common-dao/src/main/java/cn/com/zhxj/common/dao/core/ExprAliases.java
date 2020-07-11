package cn.com.zhxj.common.dao.core;

import cn.com.zhxj.common.dao.expr.Expr;

import java.util.IdentityHashMap;

public class ExprAliases {

    private static ThreadLocal<IdentityHashMap<Object, Expr>> mapThreadLocal = ThreadLocal.withInitial(IdentityHashMap::new);

    public static void addAlias(Expr expr, Object alias) {
        if (alias == null) throw new RuntimeException("alias不能为null");
        mapThreadLocal.get().put(alias, expr);
    }

    public static boolean isAliased(Object alias) {
        if (alias == null) return false;
        return mapThreadLocal.get().containsKey(alias);
    }


    public static Expr getAliasedExpr(Object alias) {
        if (alias == null) return null;
        IdentityHashMap<Object, Expr> map = null;
        try {
            map = mapThreadLocal.get();
            return map.getOrDefault(alias, null);
        } finally {
            if (map != null) {
                map.remove(alias);
            }
        }
    }
}
