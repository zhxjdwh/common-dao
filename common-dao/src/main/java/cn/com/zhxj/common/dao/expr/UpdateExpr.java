package cn.com.zhxj.common.dao.expr;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Insert表达式
 */
@Getter
public class UpdateExpr extends Expr {

    private final TableRefExpr      table;
    private final List<Entry> entries;
    private final WhereExpr where;

    public UpdateExpr(TableRefExpr table, List<Entry> entries,WhereExpr whereExpr) {
        super(ExprType.Update);
        this.table=table;
        this.entries=entries;
        this.where=whereExpr;
    }

    @Getter
    @AllArgsConstructor
    public static class Entry {
        private final FieldRefExpr field;
        private final Expr       value;
    }

}
