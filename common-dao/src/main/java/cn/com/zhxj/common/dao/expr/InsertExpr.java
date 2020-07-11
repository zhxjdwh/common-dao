package cn.com.zhxj.common.dao.expr;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Insert表达式
 */
@Getter
public class InsertExpr extends Expr {

    private final TableRefExpr      table;
    private final List<Entry> entries;

    public InsertExpr(TableRefExpr table, List<Entry> entries) {
        super(ExprType.Insert);
        this.table=table;
        this.entries=entries;
    }

    @Getter
    @AllArgsConstructor
    public static class Entry {
        private final FieldRefExpr field;
        private final Expr       value;
    }

}
