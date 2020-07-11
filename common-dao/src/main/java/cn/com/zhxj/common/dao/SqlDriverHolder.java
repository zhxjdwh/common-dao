package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.dialect.SqlCmdType;
import cn.com.zhxj.common.dao.dialect.SqlDriver;
import cn.com.zhxj.common.dao.mapping.EntityDesc;

public interface SqlDriverHolder {

    <T extends Entity> SqlDriver getSqlDriver(EntityDesc<T> entityDesc, SqlCmdType sqlType);

}
