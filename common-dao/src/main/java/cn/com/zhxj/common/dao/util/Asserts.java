package cn.com.zhxj.common.dao.util;

import cn.com.zhxj.common.dao.expr.FieldRefExpr;

public class Asserts {
    public static FieldRefExpr assertFieldNotNull(FieldRefExpr fieldRefExpr) {
        if (fieldRefExpr == null) {
            throw new RuntimeException("调用方法不正确");
        }
        return fieldRefExpr;
    }

    public static void assertAnyNotNull(Object... objects) {
        for (Object object : objects) {
            if (object != null) {
                return;
            }
        }
        throw new RuntimeException("调用方法不正确");
    }

    public static void assertAllNotNull(Object... objects) {
        for (Object object : objects) {
            if (object == null) {
                throw new RuntimeException("调用方法不正确");
            }
        }
    }

    public static void assertNotNull(Object object) {
        if (object == null) {
            throw new RuntimeException("调用方法不正确");
        }
    }
}
