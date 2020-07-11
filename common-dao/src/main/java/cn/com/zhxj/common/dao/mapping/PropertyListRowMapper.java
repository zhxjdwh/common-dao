//package cn.com.zhxj.common.dao.mapping;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.*;
//import org.springframework.core.convert.ConversionService;
//import org.springframework.dao.DataRetrievalFailureException;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.jdbc.support.JdbcUtils;
//import org.springframework.util.Assert;
//import org.springframework.util.ClassUtils;
//
//import java.beans.PropertyDescriptor;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//import java.util.List;
//
///**
// * 按Index索引去映射
// *
// * @param <T>
// */
//public class PropertyListRowMapper<T> implements RowMapper<T> {
//    protected final Logger                      logger = LoggerFactory.getLogger(this.getClass());
//    private         List<PropertyDescriptor> propertyDescriptors;
//
//    private Class<T> mappedClass;
//
//    public PropertyListRowMapper(Class<T> mappedClass, List<PropertyDescriptor> propertyDescriptors) {
//        this.propertyDescriptors = propertyDescriptors;
//        this.mappedClass=mappedClass;
//    }
//
//    public T mapRow(ResultSet rs, int rowNumber) throws SQLException {
//        Assert.state(this.mappedClass != null, "Mapped class was not specified");
//        T mappedObject = BeanUtils.instantiateClass(this.mappedClass);
//        BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(mappedObject);
//        this.initBeanWrapper(bw);
//        ResultSetMetaData rsmd = rs.getMetaData();
//        int columnCount = rsmd.getColumnCount();
//
//        for (int index = 1; index <= columnCount; ++index) {
//            PropertyDescriptor pd = propertyDescriptors.get(index - 1);
//            if (pd == null) {
//                if (rowNumber == 0 && this.logger.isDebugEnabled()) {
//                    this.logger.debug("No property found for column '" + column + "' mapped to field '" + field + "'");
//                }
//            } else {
//                try {
//                    Object value = this.getColumnValue(rs, index, pd);
//                    if (rowNumber == 0 && this.logger.isDebugEnabled()) {
//                        this.logger.debug("EntityMapping column '" + column + "' to property '" + pd.getName() + "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "'");
//                    }
//
//                    try {
//                        bw.setPropertyValue(pd.getName(), value);
//                    } catch (TypeMismatchException var14) {
//                        if (value != null || !this.primitivesDefaultedForNullValue) {
//                            throw var14;
//                        }
//                        if (this.logger.isDebugEnabled()) {
//                            this.logger.debug("Intercepted TypeMismatchException for row " + rowNumber + " and column '" + column + "' with null value when setting property '" + pd.getName() + "' of type '" + ClassUtils.getQualifiedName(pd.getPropertyType()) + "' on object: " + mappedObject, var14);
//                        }
//                    }
//
//
//                } catch (NotWritablePropertyException var15) {
//                    throw new DataRetrievalFailureException("Unable to map column '" + column + "' to property '" + pd.getName() + "'", var15);
//                }
//            }
//        }
//        return mappedObject;
//    }
//
//    protected void initBeanWrapper(BeanWrapper bw) {
//        ConversionService cs = this.getConversionService();
//        if (cs != null) {
//            bw.setConversionService(cs);
//        }
//
//    }
//
//    protected Object getColumnValue(ResultSet rs, int index, PropertyDescriptor pd) throws SQLException {
//        return JdbcUtils.getResultSetValue(rs, index, pd.getPropertyType());
//    }
//}
