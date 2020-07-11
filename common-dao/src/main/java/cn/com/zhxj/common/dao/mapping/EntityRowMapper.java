package cn.com.zhxj.common.dao.mapping;

import cn.com.zhxj.common.dao.core.Entity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.*;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class EntityRowMapper<T extends Entity> implements RowMapper<T> {
    protected final Logger logger                          = LoggerFactory.getLogger(this.getClass());
    private            Class<T>                        mappedClass;
    private            boolean                         checkFullyPopulated             = false;
    private            boolean                         primitivesDefaultedForNullValue = false;
    private            ConversionService               conversionService               = DefaultConversionService.getSharedInstance();
    private            Map<String, PropertyDescriptor> mappedFields;
    private            Set<String>                     mappedProperties;
    private EntityDesc entityDesc;


    public EntityRowMapper(EntityDesc entityDesc) {
        this.initialize(entityDesc);
    }

    public EntityRowMapper(EntityDesc entityDesc, boolean checkFullyPopulated) {
        this.initialize(entityDesc);
        this.checkFullyPopulated = checkFullyPopulated;
    }


    public final Class<T> getMappedClass() {
        return this.mappedClass;
    }

    public void setCheckFullyPopulated(boolean checkFullyPopulated) {
        this.checkFullyPopulated = checkFullyPopulated;
    }

    public boolean isCheckFullyPopulated() {
        return this.checkFullyPopulated;
    }

    public void setPrimitivesDefaultedForNullValue(boolean primitivesDefaultedForNullValue) {
        this.primitivesDefaultedForNullValue = primitivesDefaultedForNullValue;
    }

    public boolean isPrimitivesDefaultedForNullValue() {
        return this.primitivesDefaultedForNullValue;
    }

    public void setConversionService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public ConversionService getConversionService() {
        return this.conversionService;
    }

    @SuppressWarnings("unchecked")
    protected  void initialize(EntityDesc<T> entityDesc) {
        Class<T> mappedClass= entityDesc.getEntityClass();
        this.mappedClass = mappedClass;
        this.entityDesc=entityDesc;
        this.mappedFields = new HashMap();
        this.mappedProperties = new HashSet();

        for (FieldDesc column : entityDesc.getColumns()) {
            PropertyDescriptor pd = column.getPropertyDescriptor();
            if (pd.getWriteMethod() != null) {
                String colName = this.lowerCaseName(column.getColumnName());
                this.mappedFields.put(this.lowerCaseName(pd.getName()), pd);
                this.mappedFields.put(colName, pd);
                this.mappedProperties.add(pd.getName());
                this.mappedProperties.add(colName);
            }
        }
    }


    protected String lowerCaseName(String name) {
        return name.toLowerCase(Locale.US);
    }

    @Override
    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
        Assert.state(this.mappedClass != null, "Mapped class was not specified");
        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
        this.initBeanWrapper(bw);
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        Set<String> populatedProperties = this.isCheckFullyPopulated() ? new HashSet() : null;

        for(int index = 1; index <= columnCount; ++index) {
            String column = JdbcUtils.lookupColumnName(rsmd, index);
            String field = this.lowerCaseName(column.replaceAll(" ", ""));
            PropertyDescriptor pd = (PropertyDescriptor)this.mappedFields.get(field);
            if (pd == null) {
                if (rowNumber == 0 && this.logger.isDebugEnabled()) {
                    this.logger.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
                }
            } else {
                try {
                    Object value = this.getColumnValue(rs, index, pd);
                    if (rowNumber == 0 && this.logger.isDebugEnabled()) {
                        this.logger.debug("EntityMapping column '" + column + "' to property '" + pd.getName() + "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
                    }

                    try {
                        bw.setPropertyValue(pd.getName(), value);
                    } catch (TypeMismatchException var14) {
                        if (value != null || !this.primitivesDefaultedForNullValue) {
                            throw var14;
                        }

                        if (this.logger.isDebugEnabled()) {
                            this.logger.debug("Intercepted TypeMismatchException for row " + rowNumber + " and column '" + column + "' with null value when setting property '" + pd.getName() + "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "' on object: " + mappedObject, var14);
                        }
                    }

                    if (populatedProperties != null) {
                        populatedProperties.add(pd.getName());
                    }
                } catch (NotWritablePropertyException var15) {
                    throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + pd.getName() + "'", var15);
                }
            }
        }

        if (populatedProperties != null && !populatedProperties.equals(this.mappedProperties)) {
            throw new InvalidDataAccessApiUsageException("Given ResultSet does not contain all fields necessary to populate object of class [" + this.mappedClass.getName() + "]: " + this.mappedProperties);
        } else {
            return mappedObject;
        }
    }

    protected void initBeanWrapper(BeanWrapper bw) {
        ConversionService cs = this.getConversionService();
        if (cs != null) {
            bw.setConversionService(cs);
        }

    }

    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
    }
}
