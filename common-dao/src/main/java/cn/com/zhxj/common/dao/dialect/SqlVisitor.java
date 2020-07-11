package cn.com.zhxj.common.dao.dialect;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;
import java.util.stream.Collectors;

public class SqlVisitor extends ExprVisitor {

    private StringBuilder       sqlBuilder = new StringBuilder();
    private Map<String, Object> paramsMap  = new HashMap<>();
    private Stack<Scope>        scopes     = new Stack<>();
    private Stack<String>       tableAlias = new Stack<>();

    protected SqlVisitor beginScope(String start, String split, String close) {

        if (!scopes.empty()) {
            Scope scope = scopes.peek();
            if (scope.open != null) {
                sqlBuilder.append(scope.open);
                scope.open = null;
            }
        }
        scopes.push(new Scope(start, split, close));
        return this;
    }

    //where ((a=1) and ((b=2) and (c in (1,2,3)))) order by (  (max(a.fd_id) desc)  ,a.fd_code desc)

    protected SqlVisitor appendScope(String sql) {

        Scope scope = null;
        if (!scopes.empty()) {
            scope = scopes.peek();
            if (scope.open != null) {
                sqlBuilder.append(scope.open);
                scope.open = null;
            }
        }

        if (scope != null) {
            String split = scope.split;
            sqlBuilder.append(sql);
            sqlBuilder.append(split);
        }else {
            sqlBuilder.append(sql);
        }

        return this;
    }

    protected SqlVisitor endScope() {
        Scope scope = null;
        if (!scopes.empty()) {
            scope = scopes.pop();
        }
        if (scope != null) {
            String split = scope.split;
            if (split != null && !split.equals("")) {
                strapEnd(split);
            }
            if(scope.close!=null){
                sqlBuilder.append(scope.close);
            }
            scope.close=null;
        }
        if (!scopes.empty()) {
            Scope parent = scopes.peek();
            sqlBuilder.append(parent.split);
        }
        return this;
    }

    protected String startTableAlias() {
        int size = tableAlias.size();
        String alias = "t_" + size;
        tableAlias.push(alias);
        return alias;
    }

    protected String getTableAlias() {
        if (tableAlias.size() > 0) {
            tableAlias.peek();
        }
        return "";
    }

    protected void endTableAlias(String alias) {
        if (tableAlias.size() > 0 && tableAlias.peek().equals(alias)) {
            tableAlias.pop();
        }
    }

    protected void replaceSql(String sql) {
        sqlBuilder = new StringBuilder(sql);
    }

    protected void strapEnd(String split) {
        if (!sqlBuilder.toString().endsWith(split)) {
            return;
        }
        int index = sqlBuilder.toString().lastIndexOf(split);
        if (index < 0) {
            return;
        }
        sqlBuilder.delete(index, sqlBuilder.length());
    }

    protected void append(String sql) {
        sqlBuilder.append(sql);
    }

    protected String addParameter(Object param) {
        String pname = "p_" + paramsMap.size();
        paramsMap.put(pname, param);
        return pname;
    }

    public String getSql() {
        return sqlBuilder.toString();
    }

    public String getFormateSql() {
        String sql = sqlBuilder.toString();
        for (Map.Entry<String, Object> kv : paramsMap.entrySet()) {
            Object value = kv.getValue();
            sql = sql.replace(":" + kv.getKey(), formatObject(value));
        }
        return sql;
    }

    private String formatObject(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Number) {
            return value.toString();
        }

        if (value instanceof String) {
            return "'" + value.toString() + "'";
        }
        if (value instanceof Date) {
            return "timestamp'" + DateFormatUtils.format((Date) value, "yyyy-MM-dd HH:mm:ss") + "'";
        }
        if (value instanceof List) {
            List list = (List) value;
            Object listStr = list.stream().map(this::formatObject).collect(Collectors.joining(","));
            return listStr.toString();
        }
        return "?";
    }

    public Map<String, Object> getParams() {
        return paramsMap;
    }

    @AllArgsConstructor
    private static class Scope {
        private String open;
        private String split;
        private String close;
    }
}
