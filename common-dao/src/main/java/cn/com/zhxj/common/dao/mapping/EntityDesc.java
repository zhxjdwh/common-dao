package cn.com.zhxj.common.dao.mapping;

import cn.com.zhxj.common.dao.core.Entity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.RowMapper;

import java.util.*;


/**
 * 实体映射信息
 */
public class EntityDesc<T extends Entity> {

    private final Map<String, Object>    propMaps;
    private final String                 tableName;
    private final Class<T>               entityClass;
    private final List<FieldDesc>        columns;
    private       String                 schemaName;
    private       Map<String, FieldDesc> propNameMap;
    private       Map<String, FieldDesc> colNameMap;
    private       RowMapper<T>           rowMapper;
    private       String                 tableNameWithoutSchema;
    private       String[]               generatedKeys;
    private  FieldDesc[] idColumns;

    public EntityDesc(String tableName, String schemaName, Class<T> entityClass, List<FieldDesc> columns) {
        this.tableName = tableName;
        this.schemaName = schemaName;
        this.entityClass = entityClass;
        this.columns = columns;
        propMaps = new HashMap<>();
        init();
    }

    public String getTableName() {
        return tableName;
    }

    /**
     * java字段名是区分大小写的
     * @param propNameCaseSentive
     * @return
     */
    public FieldDesc getColumnByPropName(String propNameCaseSentive) {
        return propNameMap.get(propNameCaseSentive);
    }

    /**
     * 列名不区分大小写
     * @param colNameIgnoreCase
     * @return
     */
    public FieldDesc getColumnByColName(String colNameIgnoreCase) {
        return colNameMap.get(colNameIgnoreCase.toLowerCase(Locale.US));
    }

    public String getTableNameWithoutSchema() {
        if (tableNameWithoutSchema != null) {
            return tableNameWithoutSchema;
        }
        if (tableName.contains(".")) {
            String[] split = tableName.split("\\.");
            tableNameWithoutSchema = split[split.length - 1];
        }else {
            tableNameWithoutSchema=tableName;
        }
        tableNameWithoutSchema = StringUtils.strip(tableNameWithoutSchema, "\"");
        tableNameWithoutSchema = StringUtils.strip(tableNameWithoutSchema, "`");
        tableNameWithoutSchema = StringUtils.strip(tableNameWithoutSchema, "'");
        return tableNameWithoutSchema;
    }

    public FieldDesc[] getIdColumns() {
        if (idColumns == null) {
            idColumns = columns.stream().filter(FieldDesc::isId).toArray(FieldDesc[]::new);
        }
        return idColumns;
    }

    @SuppressWarnings("unchecked")
    public RowMapper<T> getRowMapper() {
        if (rowMapper == null) {
            rowMapper = new EntityRowMapper<T>(this);
        }
        return rowMapper;
    }

    /**
     * 创建实体类对象
     *
     * @return 实体类对象
     */
    public T newEntityInstance() {
        try {
            return entityClass.newInstance();
        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 添加一些额外的属性
     *
     * @param key key
     * @param val value
     */
    public void addProperty(String key, Object val) {
        synchronized (propMaps) {
            propMaps.put(key, val);
        }
    }


    public String[] getGeneratedKeys() {
        if (generatedKeys == null) {
            generatedKeys = columns.stream()
                    .filter(w -> w.isAutoIncrement() || w.getSequenceName() != null)
                    .map(FieldDesc::getColumnName).toArray(String[]::new);
        }
        return generatedKeys;
    }


    @SuppressWarnings("unchecked")
    public <TP> TP getProperty(String key) {
        TP result = null;
        try {
            result = (TP) propMaps.get(key);
        } catch (Exception ignored) {
        }
        if (result == null) {
            synchronized (propMaps) {
                result = (TP) propMaps.get(key);
            }
        }
        return result;
    }

    /**
     * 实体类
     *
     * @return 实体类
     */
    public Class<T> getEntityClass() {
        return entityClass;
    }

    public List<FieldDesc> getColumns() {
        return columns;
    }

    public Iterator<FieldDesc> getColumnsIterator() {
        return new Iterator<FieldDesc>() {
            private int index = -1;

            @Override
            public boolean hasNext() {
                return index + 1 >= columns.size();
            }

            @Override
            public FieldDesc next() {
                FieldDesc fieldDesc = columns.get(index);
                index++;
                return fieldDesc;
            }
        };
    }

    /**
     * schema name, 如果表名包含了 schema，那就取表名的
     *
     * @return schema name
     */
    public String getSchemaName() {
        if (StringUtils.isBlank(schemaName) && StringUtils.isNotEmpty(tableName) && tableName.contains(".")) {
            schemaName = tableName.split(".")[0];
        }
        return schemaName;
    }

    private void init() {
        propNameMap = new HashMap<>();
        colNameMap = new HashMap<>();
        for (FieldDesc column : columns) {
            propNameMap.put(column.getPropertyDescriptor().getName(), column);
            propNameMap.put(column.getPropertyDescriptor().getName().toLowerCase(Locale.US), column);
            colNameMap.put(column.getColumnName(), column);
            colNameMap.put(column.getColumnName().toLowerCase(Locale.US), column);
        }
    }


}
