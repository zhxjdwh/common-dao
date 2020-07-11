package cn.com.zhxj.common.dao.core;

import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import lombok.Getter;


@Getter
public class FieldValueHolder {
    private EntityDesc entityDesc;
    private FieldDesc  fieldDesc;
    private Object     fieldValue;
    private DbGenValue dbGenValue;
    /**
     * 是否是用户行为
     * 比如 用户在update的时候 给这个字段赋值了，那就是true
     * 如果 用户在update的时候 没有对这个字段做任何操作，那就是false
     */
    private boolean    userRecommend;

    public FieldValueHolder(EntityDesc entityDesc, FieldDesc fieldDesc, Object fieldValue, boolean userRecommend) {
        this.entityDesc = entityDesc;
        this.fieldDesc = fieldDesc;
        this.fieldValue = fieldValue;
        this.userRecommend = userRecommend;
    }

    /**
     * 回填到对象的值
     * @param value
     */
    public void setFieldValue(Object value) {
        fieldValue = value;
    }

    /**
     * 如果值是由数据库生成
     * @param dbGenValue
     */
    public void setDbGenValue(DbGenValue dbGenValue) {
        this.dbGenValue=dbGenValue;
    }
}
