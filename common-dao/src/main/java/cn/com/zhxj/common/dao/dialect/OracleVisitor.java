package cn.com.zhxj.common.dao.dialect;


import cn.com.zhxj.common.dao.core.Pagination;
import cn.com.zhxj.common.dao.expr.Expr;
import cn.com.zhxj.common.dao.core.Limited;
import cn.com.zhxj.common.dao.expr.MethodCallExpr;
import cn.com.zhxj.common.dao.expr.QueryExpr;

import java.text.MessageFormat;

public class OracleVisitor extends AnsiSqlVisitor {

    @Override
    protected void processPagedQuery(QueryExpr expr) {
        Pagination pagination = expr.getPagination();

        int start = pagination.getStartInclude();
        int end = pagination.getEndExclude();
        String pageSql="SELECT * FROM (SELECT ROWNUM R_NUM,T_P.* FROM ({0}) T_P WHERE ROWNUM<={1} ) WHERE R_NUM>{2}";

        String sql = getSql();
        replaceSql(MessageFormat.format(pageSql,sql,end+"",start+""));
    }

    @Override
    protected void processLimitedQuery(QueryExpr expr) {
        Limited limited = expr.getLimited();
        String limitedSql="SELECT T_L.* FROM ({0}) T_L WHERE ROWNUM<={1}";
        String sql = getSql();
        replaceSql(MessageFormat.format(limitedSql,sql,limited.getLimit()+""));
    }

    @Override
    protected Expr visitMethodCall(MethodCallExpr expr) {
        if(expr.getStandardMethod()!=null){
            switch (expr.getStandardMethod()){
                case CurrentDateTime: appendScope("SYSDATE") ; return expr;
                case CurrentTimestamp: appendScope("CURRENT_TIMESTAMP") ; return expr;
                case CurrentDateTimeUtc: appendScope("SYS_EXTRACT_UTC(SYSTIMESTAMP)") ; return expr;
                case CurrentTimestampUtc: appendScope("SYS_EXTRACT_UTC(SYSTIMESTAMP)") ; return expr;
            }
        }
        return super.visitMethodCall(expr);
    }


}
