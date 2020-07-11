package cn.com.zhxj.common.dao.dialect;

import cn.com.zhxj.common.dao.core.*;
import cn.com.zhxj.common.dao.expr.DeleteExpr;
import cn.com.zhxj.common.dao.expr.QueryExpr;
import cn.com.zhxj.common.dao.expr.UpdateExpr;
import cn.com.zhxj.common.dao.expr.WhereExpr;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import cn.com.zhxj.common.dao.util.FastBeanUtils;
import cn.com.zhxj.common.dao.util.ProxyUtils;
import cn.com.zhxj.common.dao.core.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public abstract class SqlDriver {

    protected static ConversionService           conversionService = DefaultConversionService.getSharedInstance();
    private static   HashMap<DataSource, String> dbNameMap         = new HashMap<>();
    protected        NamedParameterJdbcTemplate  namedParameterJdbcTemplate;

    protected SqlDriver(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public static SqlDriver getByJdbcTemplate(JdbcTemplate jdbcTemplate) {
        DataSource dataSource = jdbcTemplate.getDataSource();
        String dbName = dbNameMap.getOrDefault(dataSource, null);
        if (dbName == null) {
            try {
                String name = JdbcUtils.extractDatabaseMetaData(dataSource, "getDatabaseProductName").toString().toLowerCase();
                HashMap<DataSource, String> dataSourceStringHashMap = new HashMap<>(dbNameMap);
                dataSourceStringHashMap.put(dataSource, name);
                dbNameMap = dataSourceStringHashMap;
                dbName = name;
            } catch (MetaDataAccessException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        if (dbName.contains("oracle")) {
            return new OracleDriver(namedParameterJdbcTemplate);
        }
        if (dbName.contains("mysql")) {
            return new MySqlDriver(namedParameterJdbcTemplate);
        }
        throw new RuntimeException("不支持数据库:" + dbName);
    }

    /**
     * 批量insert 不会 回填数据库生成的key
     *
     * @param entities
     * @param entityDesc
     * @param <T>
     * @return
     */
    public <T extends Entity> void insertBatch(List<T> entities, EntityDesc<T> entityDesc) {
        insert(entities, entityDesc, ExamplePolicy.INCLUDE_NULL);
    }

    /**
     * 单条insert 回填数据库生成的key
     *
     * @param entity
     * @param entityDesc
     * @param policy
     * @param <T>
     * @return
     */
    public <T extends Entity> int insert(T entity, EntityDesc<T> entityDesc, ExamplePolicy policy) {
        return insert(Collections.singletonList(entity), entityDesc, policy);
    }

    /**
     * 单个insert 的行为  1.回填key 2.支持 ExamplePolicy
     * 批量insert 的行为  1.不回填key 2.不支持支持 ExamplePolicy
     *
     * @param entities
     * @param entityDesc
     * @param policy     批量insert必须为 ExamplePolicy.INCLUDE_NULL
     * @return
     */
    protected int insert(List<?> entities, EntityDesc<?> entityDesc, ExamplePolicy policy) {
        try {
            if (policy == null || entities.size() > 1) {
                policy = ExamplePolicy.INCLUDE_NULL;
            }
            List<Tuple2<String, Map>> sqlList = new ArrayList<>();
            List<Map> beans = entities.stream().map(FastBeanUtils::toBeanMap).collect(Collectors.toList());
            for (Map bean : beans) {
                StringBuilder fieldSql = new StringBuilder();
                StringBuilder valueSql = new StringBuilder();
                Map<String, Object> paramMap = new HashMap<>();
                for (FieldDesc column : entityDesc.getColumns()) {
                    String name = column.getPropertyDescriptor().getName();
                    if (column.isAutoIncrement()) {
                        continue;
                    }
                    if (column.getSequenceName() != null) {
                        fieldSql.append(column.getColumnName()).append(",");
                        valueSql.append(column.getSequenceName()).append(".NEXTVAL").append(",");
                        continue;
                    }
                    if (!column.isInsertable()) {
                        if (log.isDebugEnabled()) {
                            log.debug("字段注解insertable为false,忽略:" + column.getColumnName() + " " + name);
                        }
                        continue;
                    }

                    Object value = bean.get(name);

                    if (!policy.canAccept(value) && column.getInsertConsumer() == null) {
                        continue;
                    }
                    fieldSql.append(column.getColumnName()).append(",");
                    if (column.getInsertConsumer() != null) {
                        FieldValueHolder ctx = new FieldValueHolder(entityDesc, column, value, value != null);
                        column.getInsertConsumer().accept(ctx);
                        if (!Objects.equals(ctx.getFieldValue(), value)) {
                            //回填字段值
                            bean.put(name, convertIfNecessary(ctx.getFieldValue(), column.getFieldType()));
                        }
                        //处理一些特殊值，比如数据库生成的值 数据库时间之类的
                        if (ctx.getDbGenValue() != null) {
                            valueSql.append(getDbGenerateSql(entityDesc, column, ctx.getDbGenValue())).append(",");
                        } else {
                            valueSql.append(":").append(name).append(",");
                            paramMap.put(name, ObjectUtils.defaultIfNull(ctx.getFieldValue(), value));
                        }
                    } else {
                        valueSql.append(":").append(name).append(",");
                        paramMap.put(name, value);
                    }
                }
                StringBuilder sql = new StringBuilder();
                sql.append("INSERT INTO ").append(entityDesc.getTableName()).append(" (");
                sql.append(fieldSql.deleteCharAt(fieldSql.length() - 1));
                sql.append(") ");
                sql.append("VALUES(");
                sql.append(valueSql.deleteCharAt(valueSql.length() - 1));
                sql.append(") ");

                sqlList.add(Tuple.of(sql.toString(), paramMap));
            }

            if (sqlList.size() > 1) {
                //批量insert 只取第一条sql 不回填 自增id/序列id
                Tuple2<String, Map> firstSqlTuple = sqlList.get(0);
                Map[] maps = sqlList.stream().map(w -> w.f2).toArray(Map[]::new);
                int[] rows = namedParameterJdbcTemplate.batchUpdate(firstSqlTuple.f1, maps);
                return Arrays.stream(rows).sum();
            } else {
                //单条insert
                int row = 0;
                Tuple2<String, Map> firstSqlTuple = sqlList.get(0);
                String[] generatedKeys = entityDesc.getGeneratedKeys();
                if (generatedKeys != null && generatedKeys.length > 0) {
                    GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();
                    row = namedParameterJdbcTemplate.update(firstSqlTuple.f1, new MapSqlParameterSource(firstSqlTuple.f2), generatedKeyHolder, generatedKeys);
                    //字段回填
                    if (generatedKeyHolder.getKeys() != null) {
                        Map beanMap = beans.get(0);
                        Map<String, Object> keys = generatedKeyHolder.getKeys();
                        for (String keyColName : generatedKeys) {
                            Object keyVal = keys.get(keyColName);
                            if (keyVal == null) continue;
                            FieldDesc keyCol = entityDesc.getColumnByColName(keyColName);
                            beanMap.put(keyCol.getPropertyDescriptor().getName(), convertIfNecessary(keyVal, keyCol.getFieldType()));
                        }
                    }
                } else {
                    row = namedParameterJdbcTemplate.update(firstSqlTuple.f1, firstSqlTuple.f2);
                }
                return row;
            }
        } finally {
            onComplete();
        }
    }


    public abstract String getDbGenerateSql(EntityDesc<?> entityDesc, FieldDesc fieldDesc, DbGenValue dbGenValue);


    protected Object convertIfNecessary(Object val, Class<?> targetType) {
        if (val == null || val.getClass() == targetType) return val;
        return conversionService.convert(val, targetType);
    }


    protected abstract SqlVisitor getExprVisitor();


    @SuppressWarnings("unchecked")
    public <T> List<T> queryForEntity(QueryExpr expr, RowMapper<T> rowMapper) {
        try {
            checkWhereSafe(expr.getWhere());
            SqlVisitor exprVisitor = getExprVisitor();
            exprVisitor.visit(expr);
            String sql = exprVisitor.getSql();
            Map<String, Object> params = exprVisitor.getParams();
            if (log.isDebugEnabled()) {
                log.debug(exprVisitor.getFormateSql());
            }
            return namedParameterJdbcTemplate.query(sql, params, rowMapper);
        } finally {
            onComplete();
        }
    }

    public <T> T queryForObject(QueryExpr expr, Class<T> tClass) {
        try {
            checkWhereSafe(expr.getWhere());
            SqlVisitor exprVisitor = getExprVisitor();
            exprVisitor.visit(expr);
            String sql = exprVisitor.getSql();
            Map<String, Object> params = exprVisitor.getParams();
            if (log.isDebugEnabled()) {
                log.debug(exprVisitor.getFormateSql());
            }
            return namedParameterJdbcTemplate.queryForObject(sql, params, tClass);
        } finally {
            onComplete();
        }
    }

    public int delete(DeleteExpr expr) {
        try {
            checkWhereSafe(expr.getWhere());
            SqlVisitor exprVisitor = getExprVisitor();
            exprVisitor.visit(expr);
            String sql = exprVisitor.getSql();
            Map<String, Object> params = exprVisitor.getParams();
            if (log.isDebugEnabled()) {
                log.debug(exprVisitor.getFormateSql());
            }
            return namedParameterJdbcTemplate.update(sql, params);
        } finally {
            onComplete();
        }
    }

    public int update(UpdateExpr expr) {
        try {
            SqlVisitor exprVisitor = getExprVisitor();
            exprVisitor.visit(expr);
            String sql = exprVisitor.getSql();
            Map<String, Object> params = exprVisitor.getParams();
            if (log.isDebugEnabled()) {
                log.debug(exprVisitor.getFormateSql());
            }
            return namedParameterJdbcTemplate.update(sql, params);
        } finally {
            onComplete();
        }
    }

    public <T extends Entity> int batchUpdate(List<T> entities, EntityDesc<T> entityDesc) {
        try {
            List<Tuple2<String, Map>> sqlList = new ArrayList<>();
            List<Map> beans = entities.stream().map(FastBeanUtils::toBeanMap).collect(Collectors.toList());
            for (Map bean : beans) {
                StringBuilder sql = new StringBuilder();
                StringBuilder where = new StringBuilder();
                sql.append("UPDATE ").append(entityDesc.getTableName()).append(" SET ");
                Map<String, Object> paramMap = new HashMap<>();

                for (FieldDesc column : entityDesc.getColumns()) {
                    String name = column.getPropertyDescriptor().getName();
                    if (column.isId()) {
                        Object id = bean.get(name);
                        if (id == null) {
                            throw new IllegalArgumentException("update，但是id值为null");
                        }
                        where.append(column.getColumnName()).append("=").append(":").append(name).append(" AND ");
                        paramMap.put(name, id);
                        continue;
                    }
                    if (!column.canUpdate()) {
                        if (log.isDebugEnabled()) {
                            log.debug("字段注解不可update为false,忽略:" + column.getColumnName() + " " + name);
                        }
                        continue;
                    }

                    Object value = bean.get(name);
                    sql.append(column.getColumnName()).append("="); // FD_NAME=
                    if (column.getUpdateConsumer() != null) {
                        FieldValueHolder ctx = new FieldValueHolder(entityDesc, column, value, value != null);
                        column.getUpdateConsumer().accept(ctx);
                        if (!Objects.equals(ctx.getFieldValue(), value)) {
                            //回填字段值
                            bean.put(name, convertIfNecessary(ctx.getFieldValue(), column.getFieldType()));
                        }
                        //处理一些特殊值，比如数据库生成的值 数据库时间之类的
                        if (ctx.getDbGenValue() != null) {
                            sql.append(getDbGenerateSql(entityDesc, column, ctx.getDbGenValue())).append(",");
                        } else {
                            sql.append(":").append(name).append(",");
                            paramMap.put(name, ObjectUtils.defaultIfNull(ctx.getFieldValue(), value));
                        }
                    } else {
                        sql.append(":").append(name).append(",");
                        paramMap.put(name, value);
                    }
                }
                sql.deleteCharAt(sql.length() - 1);
                if (where.length() < 1) {
                    throw new IllegalArgumentException("update操作where条件不合法,请检查Id注解以及Id值是否正确");
                }
                sql.append(" WHERE ").append(where.substring(0,where.length() - 4));
                sqlList.add(Tuple.of(sql.toString(), paramMap));
            }

            if (sqlList.size() > 1) {
                //批量Update 只取第一条sql
                Tuple2<String, Map> firstSqlTuple = sqlList.get(0);
                Map[] maps = sqlList.stream().map(w -> w.f2).toArray(Map[]::new);
                int[] rows = namedParameterJdbcTemplate.batchUpdate(firstSqlTuple.f1, maps);
                return Arrays.stream(rows).sum();
            } else {
                Tuple2<String, Map> firstSqlTuple = sqlList.get(0);
                int row = namedParameterJdbcTemplate.update(firstSqlTuple.f1, firstSqlTuple.f2);
                return row;
            }
        } finally {
            onComplete();
        }
    }

    public void onComplete() {
        ProxyUtils.clean();
    }

    /**
     * 检查Where表达式是是否安全 至少有一个字段引用才认为是安全的
     *
     * @param expr
     */
    protected void checkWhereSafe(WhereExpr expr) {
        if (expr == null) throw new IllegalArgumentException("Where条件不合法");
        WhereSafeVisitor whereSafeVisitor = new WhereSafeVisitor();
        whereSafeVisitor.visit(expr);
        if (!whereSafeVisitor.isSafe()) {
            throw new IllegalArgumentException("Where条件不合法");
        }
    }
}
