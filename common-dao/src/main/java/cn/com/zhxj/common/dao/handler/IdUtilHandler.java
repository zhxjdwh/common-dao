package cn.com.zhxj.common.dao.handler;

import cn.com.zhxj.common.dao.core.FieldValueHolder;
import cn.com.zhxj.common.dao.util.IdUtils;

import java.util.Locale;

public class IdUtilHandler implements ValueHandler {
    @Override
    public void process(FieldValueHolder holder) {
        if(holder.getFieldValue()==null){
            String tableName = holder.getEntityDesc().getTableNameWithoutSchema().toUpperCase(Locale.US);
            String id = IdUtils.newId(tableName);
            holder.setFieldValue(id);
        }
    }
}
