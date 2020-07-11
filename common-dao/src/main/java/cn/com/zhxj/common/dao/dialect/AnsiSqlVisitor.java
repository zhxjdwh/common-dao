package cn.com.zhxj.common.dao.dialect;


import cn.com.zhxj.common.dao.core.ExprAliases;
import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;

import java.util.List;

import static cn.com.zhxj.common.dao.core.ExprFactory.$const;

/**
 * 符合 AnsiSql 标准
 */
public class AnsiSqlVisitor extends SqlVisitor {


    @Override
    protected Expr visitLogical(LogicalExpr expr) {
        if (expr.getLeft() == null && expr.getRight() == null) {
            return expr;
        }
        if (expr.getLeft() == null) {
            return expr.getRight();
        }
        if (expr.getRight() == null) {
            return expr.getLeft();
        }
        beginScope("("," ",")");
        visit(expr.getLeft());
        String op = null;
        switch (expr.getType().getTypeName()) {
            case ExprTypeName.And:
                op = "AND";
                break;
            case ExprTypeName.Or:
                op = "OR";
                break;
            case ExprTypeName.Not:
                throw new IllegalArgumentException("不支持NOT");
        }
        appendScope(op);
        visit(expr.getRight());
        endScope();
        return expr;
    }

    @Override
    protected Expr visitConstant(ConstantExpr expr) {
        //a=? or c=? and $field=()
        Object data = expr.getData();
        if (data == null) {
            appendScope("NULL");
            return expr;
        }

        Expr aliasedExpr = ExprAliases.getAliasedExpr(data);
        if (aliasedExpr != null) {
            return aliasedExpr;
        }
        if (data instanceof List) {
            for (Object item : ((List) data)) {
                appendScope(":" + addParameter(item) + "");
            }
            return expr;
        }
        appendScope(":" + addParameter(data));
        return expr;
    }


    @Override
    protected Expr visitBinary(BinaryExpr expr) {
        if (expr.getLeft() == null && expr.getRight() == null) {
            return expr;
        }
        if (expr.getLeft() == null) {
            return expr.getRight();
        }
        if (expr.getRight() == null) {
            return expr.getLeft();
        }

        ExprType type = expr.getType();
        beginScope("("," ",")");
        visit(expr.getLeft());


        switch (type.getTypeName()) {
            case ExprTypeName.Between:
                beginScope("BETWEEN "," AND","");
                visit(expr.getRight());
                endScope();
                break;
            case ExprTypeName.NotBetween:
                beginScope("NOT BETWEEN "," AND","");
                visit(expr.getRight());
                endScope();
                break;
            case ExprTypeName.In:
                beginScope("IN ("," ,",")");
                visit(expr.getRight());
                endScope();
                break;
            case ExprTypeName.NotIn:
                beginScope("NOT IN ("," ,",")");
                visit(expr.getRight());
                endScope();
                break;
            default: {
                Expr right=expr.getRight();
                switch (type.getTypeName()) {
                    case ExprTypeName.Gt:
                        appendScope(">");
                        break;
                    case ExprTypeName.Gte:
                        appendScope(">=");
                        break;
                    case ExprTypeName.Lt:
                        appendScope("<");
                        break;
                    case ExprTypeName.Lte:
                        appendScope("<=");
                        break;
                    case ExprTypeName.Eq:
                        appendScope("=");
                        break;
                    case ExprTypeName.NotEq:
                        appendScope("<>");
                        break;
                    case ExprTypeName.Like:
                        appendScope("LIKE");
                        break;
                    case ExprTypeName.StartWith:
                        appendScope("LIKE");
                        right=new MethodCallExpr(null, StandardMethod.Concat,new Expr[]{expr.getRight(),$const("%")});
                        break;
                    case ExprTypeName.EndWith:
                        appendScope("LIKE");
                        right=new MethodCallExpr(null,StandardMethod.Concat,new Expr[]{$const("%"),expr.getRight()});
                        break;
                    case ExprTypeName.Contain:
                        appendScope("LIKE");
                        MethodCallExpr m1 = new MethodCallExpr(null, StandardMethod.Concat, new Expr[]{$const("%"), expr.getRight()});
                        MethodCallExpr m2 = new MethodCallExpr(null, StandardMethod.Concat, new Expr[]{m1,$const("%")});
                        right=m2;
                        break;
                    case ExprTypeName.NotLike:
                        appendScope("NOT LIKE");
                        break;
                    case ExprTypeName.Add:
                        appendScope("+");
                        break;
                    case ExprTypeName.Sub:
                        appendScope("-");
                        break;
                    case ExprTypeName.Mul:
                        appendScope("*");
                        break;
                    case ExprTypeName.Div:
                        appendScope("/");
                        break;
                }
                visit(right);
                break;
            }
        }
        endScope();
        return expr;
    }

    @Override
    protected Expr visitFieldRef(FieldRefExpr expr) {
        appendScope(expr.getField());
        return expr;
    }

    @Override
    protected Expr visitSql(SqlExpr expr) {
        appendScope(expr.getSql());
        return expr;
    }

    @Override
    protected Expr visitSelect(SelectExpr expr) {
        beginScope("SELECT" + (expr.isDistinct() ? " DISTINCT " : " ")," ,","");
        Expr[] fields = expr.getFields();
        for (Expr field : fields) {
            visit(field);
        }
        endScope();
        return expr;
    }

    @Override
    protected Expr visitWhere(WhereExpr expr) {
        beginScope("WHERE "," ","");
        visit(expr.getExpr());
        endScope();
        return expr;
    }

    @Override
    protected Expr visitOrderBy(OrderByExpr expr) {
        beginScope("ORDER BY "," ,","");
        for (OrderEntryExpr orderEntryExpr : expr.getOrderEntryExprs()) {
            visit(orderEntryExpr);
        }
        endScope();
        return expr;
    }

    @Override
    protected Expr visitOrderEntry(OrderEntryExpr expr) {
        beginScope(""," ","");
        visit(expr.getExpr());
        appendScope(expr.isDesc() ? "DESC" : "ASC");
        endScope();
        return expr;
    }

    @Override
    protected Expr visitUnary(UnaryExpr expr) {
        beginScope(" "," ","");
        visit(expr.getExpr());
        switch (expr.getType().getTypeName()) {
            case ExprTypeName.IsNull:
                appendScope("IS NULL");
                break;
            case ExprTypeName.IsNotNull:
                appendScope("IS NOT NULL");
                break;
//            case ExprTypeName.Call     :                  break;
//            case ExprTypeName.Negative :                  break;
            default:
                throw new IllegalArgumentException("不支持:" + expr.getType().getTypeName());
        }
        endScope();
        return expr;
    }

    @Override
    protected Expr visitTableRef(TableRefExpr expr) {
        //对于mysql 表别名是必须的
        appendScope(expr.getTable() + " " + getTableAlias());
        return expr;
    }

    @Override
    protected Expr visitQuery(QueryExpr expr) {
        String alias = startTableAlias();
        beginScope(" "," ","");
        visit(expr.getSelect());
        visit(expr.getFrom());
        visit(expr.getWhere());
        if (expr.getOrderBy() != null) {
            visit(expr.getOrderBy());
        }
        endScope();
        endTableAlias(alias);

        if (expr.getLimited() != null) {
            processLimitedQuery(expr);
            return expr;
        }
        if (expr.getPagination() != null) {
            processPagedQuery(expr);
            return expr;
        }
        return expr;
    }

    /**
     * 处理Limit查询
     */
    protected void processLimitedQuery(QueryExpr expr) {
        throw new IllegalArgumentException("标准SQL解析器不支持Limited查询");
    }

    /**
     * 处理Limit查询
     */
    protected void processPagedQuery(QueryExpr expr) {
        throw new IllegalArgumentException("标准SQL解析器不支持分页查询");
    }

    @Override
    protected Expr visitFrom(FromExpr expr) {
        beginScope("FROM "," "," ");
        visit(expr.getExpr());
        endScope();
        return expr;
    }

    @Override
    protected Expr visitDelete(DeleteExpr expr) {
        beginScope("DELETE FROM "," ","");
        visit(expr.getTable());
        visit(expr.getWhere());
        endScope();
        return expr;
    }

    @Override
    protected Expr visitMethodCall(MethodCallExpr expr) {
        StandardMethod standardMethod = expr.getStandardMethod();
        if (standardMethod != null) {

            switch (standardMethod) {
                case Count:
                    appendScope("COUNT(*)");
                    return expr;
            }

            String begin = "";
            switch (standardMethod) {
                case CountDistinct:
                    begin = "COUNT(DISTINCT ";
                    break;
                case Sum:
                    begin = "SUM(";
                    break;
                case Avg:
                    begin = "AVG(";
                    break;
                case Min:
                    begin = "MIN(";
                    break;
                case Max:
                    begin = "MAX(";
                    break;
                case Len:
                    begin = "LEN(";
                    break;
                case Lower:
                    begin = "LOWER(";
                    break;
                case Upper:
                    begin = "UPPER(";
                    break;
                case Abs:
                    begin = "ABS(";
                    break;
                case Concat:
                    begin = "CONCAT(";
                    break;
                case Replace:
                    begin = "REPLACE(";
                    break;
            }
            beginScope(begin," ,",")");
            Expr[] params = expr.getParams();
            if(params!=null){
                for (Expr param : params) {
                    visit(param);
                }
            }
            endScope();

        } else {

            String begin = expr.getMethod() + "(";
            beginScope(begin," ,",")");
            Expr[] params = expr.getParams();
            for (Expr param : params) {
                visit(param);
            }
            endScope();
        }
        return expr;
    }

    @Override
    protected Expr visitInsert(InsertExpr expr) {
        beginScope("INSERT INTO "," ","");
        visit(expr.getTable());

        beginScope("("," ,",")");
        for (InsertExpr.Entry entry : expr.getEntries()) {
            visit(entry.getField());
        }
        endScope();

        beginScope("VALUES("," ,",")");

        for (InsertExpr.Entry entry : expr.getEntries()) {
            visit(entry.getValue());
        }
        endScope();
        endScope();
        return expr;
    }

    @Override
    protected Expr visitUpdate(UpdateExpr expr) {
        beginScope("UPDATE "," ","");
        visit(expr.getTable());
        beginScope("SET "," ,","");
        for (UpdateExpr.Entry entry : expr.getEntries()) {
            beginScope(""," ","");
            visit(entry.getField());
            appendScope("=");
            visit(entry.getValue());
            endScope();
        }
        endScope();
        visit(expr.getWhere());
        endScope();
        return expr;
    }


}
