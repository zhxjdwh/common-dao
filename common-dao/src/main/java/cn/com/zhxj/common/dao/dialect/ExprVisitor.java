package cn.com.zhxj.common.dao.dialect;


import cn.com.zhxj.common.dao.expr.*;
import cn.com.zhxj.common.dao.expr.*;

import java.util.List;

import static cn.com.zhxj.common.dao.expr.ExprTypeName.*;

public class ExprVisitor {

    public Expr visit(Expr expr) {
        if (expr == null) {
            return expr;
        }
        Expr exprRet = null;
        ExprType type = expr.getType();

        switch (type.getTypeName()) {
            case Constant:
                exprRet = visitConstant((ConstantExpr) expr);
                break;
            case From:
                exprRet = visitFrom((FromExpr) expr);
                break;
            case Query:
                exprRet = visitQuery((QueryExpr) expr);
                break;
            case Insert:
                exprRet = visitInsert((InsertExpr) expr);
                break;
            case Delete:
                exprRet = visitDelete((DeleteExpr) expr);
                break;
            case Update:
                exprRet = visitUpdate((UpdateExpr) expr);
                break;
            case FieldRef:
                exprRet = visitFieldRef((FieldRefExpr) expr);
                break;
            case TableRef:
                exprRet = visitTableRef((TableRefExpr) expr);
                break;
//            case GroupBy   :exprRet=visit((GrEx) expr);break;
            case Select:
                exprRet = visitSelect((SelectExpr) expr);
                break;
            case Where:
                exprRet = visitWhere((WhereExpr) expr);
                break;
            case Sql:
                exprRet = visitSql((SqlExpr) expr);
                break;

            case And:
            case Or:
            case Not:
                exprRet = visitLogical((LogicalExpr) expr);
                break;
            case Gt:
            case Gte:
            case Lt:
            case Lte:
            case Eq:
            case NotEq:
            case Like:
            case NotLike:
            case Between:
            case NotBetween:
            case In:
            case NotIn:
            case Add:
            case Sub:
            case Mul:
            case Div:
            case StartWith:
            case EndWith:
            case Contain:
                exprRet = visitBinary((BinaryExpr) expr);
                break;
            case IsNull:
            case IsNotNull:
            case Call:
            case Negative:
                exprRet = visitUnary((UnaryExpr) expr);
                break;
            case Asc:
            case Desc:
                exprRet = visitOrderEntry((OrderEntryExpr) expr);
                break;
            case OrderBy:
                exprRet = visitOrderBy((OrderByExpr) expr);
                break;

            case MethodCall:
                exprRet = visitMethodCall((MethodCallExpr) expr);
                break;
        }
        if (exprRet != expr) {
            visit(exprRet);
        }
        return exprRet;
    }

    protected Expr visitBinary(BinaryExpr expr) {
        visit(expr.getLeft());
        visit(expr.getRight());
        return expr;
    }

    protected Expr visitLogical(LogicalExpr expr) {
        visit(expr.getLeft());
        visit(expr.getRight());
        return expr;
    }

    protected Expr visitConstant(ConstantExpr expr) {
        return expr;
    }

    protected Expr visitOrderBy(OrderByExpr expr) {
        if (expr.getOrderEntryExprs() == null) return expr;
        for (OrderEntryExpr orderEntryExpr : expr.getOrderEntryExprs()) {
            visit(orderEntryExpr);
        }
        return expr;
    }

    protected Expr visitOrderEntry(OrderEntryExpr expr) {
        return expr;
    }

    protected Expr visitUnary(UnaryExpr expr) {
        visit(expr.getExpr());
        return expr;
    }

    protected Expr visitFieldRef(FieldRefExpr expr) {
        return expr;
    }

    protected Expr visitFrom(FromExpr expr) {
        visit(expr.getExpr());
        return expr;
    }

    protected Expr visitQuery(QueryExpr expr) {
        visit(expr.getSelect());
        visit(expr.getFrom());
        visit(expr.getWhere());
        visit(expr.getOrderBy());
        return expr;
    }

    protected Expr visitInsert(InsertExpr expr) {
        List<InsertExpr.Entry> entries = expr.getEntries();
        if (entries != null) {
            for (InsertExpr.Entry entry : entries) {
                visit(entry.getField());
                visit(entry.getValue());
            }
        }
        return expr;
    }

    protected Expr visitTableRef(TableRefExpr expr) {
        return expr;
    }

    protected Expr visitDelete(DeleteExpr expr) {
        visit(expr.getTable());
        visit(expr.getWhere());
        return expr;
    }

    protected Expr visitSelect(SelectExpr expr) {
        for (Expr field : expr.getFields()) {
            visit(field);
        }
        return expr;
    }

    protected Expr visitWhere(WhereExpr expr) {
        visit(expr.getExpr());
        return expr;
    }

    protected Expr visitSql(SqlExpr expr) {

        return expr;
    }

    protected Expr visitMethodCall(MethodCallExpr expr) {
        Expr[] params = expr.getParams();
        if (params != null) {
            for (Expr param : params) {
                visit(param);
            }
        }
        return expr;
    }

    protected Expr visitUpdate(UpdateExpr expr) {
        visit(expr.getTable());
        for (UpdateExpr.Entry entry : expr.getEntries()) {
            visit(entry.getField());
            visit(entry.getValue());
        }
        visit(expr.getWhere());
        return expr;
    }


}
