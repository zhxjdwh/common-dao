package cn.com.zhxj.common.dao.dialect;



import cn.com.zhxj.common.dao.core.Pagination;
import cn.com.zhxj.common.dao.expr.Expr;
import cn.com.zhxj.common.dao.expr.MethodCallExpr;
import cn.com.zhxj.common.dao.expr.QueryExpr;

import java.text.MessageFormat;

public class MysqlVisitor extends AnsiSqlVisitor {

    @Override
    protected void processPagedQuery(QueryExpr expr) {
        Pagination pagination = expr.getPagination();
        String pageSql="SELECT T_P.* FROM ({0}) T_P LIMIT {1},{2}";
        String sql = getSql();
        replaceSql(MessageFormat.format(pageSql,sql, pagination.getStartInclude()+"", pagination.getPageSize()+""));
    }

    @Override
    protected void processLimitedQuery(QueryExpr expr) {
        String pageSql="SELECT T_L.* FROM ({0}) T_L LIMIT {1}";
        String sql = getSql();
        replaceSql(MessageFormat.format(pageSql,sql,expr.getLimited().getLimit()+""));
    }

    @Override
    protected Expr visitMethodCall(MethodCallExpr expr) {
        if(expr.getStandardMethod()!=null){
            switch (expr.getStandardMethod()){
                case CurrentDateTime: appendScope("NOW()") ; return expr;
                case CurrentTimestamp: appendScope("CURRENT_TIMESTAMP()") ; return expr;
                case CurrentDateTimeUtc: appendScope("UTC_TIMESTAMP()") ; return expr;
                case CurrentTimestampUtc: appendScope("UTC_TIMESTAMP()") ; return expr;
            }
        }
        return super.visitMethodCall(expr);
    }


}
