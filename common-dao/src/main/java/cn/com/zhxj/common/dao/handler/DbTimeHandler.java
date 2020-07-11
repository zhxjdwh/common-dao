package cn.com.zhxj.common.dao.handler;

import cn.com.zhxj.common.dao.core.DbGenValue;
import cn.com.zhxj.common.dao.core.FieldValueHolder;

import java.util.Date;

public class DbTimeHandler implements ValueHandler {
    @Override
    public void process(FieldValueHolder holder) {
        holder.setFieldValue(new Date());
        holder.setDbGenValue(DbGenValue.CURRENT_TIMESTAMP);
    }
}
