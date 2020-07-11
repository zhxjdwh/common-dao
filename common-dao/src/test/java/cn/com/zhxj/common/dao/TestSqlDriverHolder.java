package cn.com.zhxj.common.dao;

import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.dialect.SqlCmdType;
import cn.com.zhxj.common.dao.dialect.SqlDriver;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestSqlDriverHolder implements SqlDriverHolder {

    private JdbcTemplate jdbcTemplate;

    public TestSqlDriverHolder(JdbcTemplate jdbcTemplate){
        this.jdbcTemplate=jdbcTemplate;
    }

    @Override
    public <T extends Entity> SqlDriver getSqlDriver(EntityDesc<T> entityDesc, SqlCmdType sqlType) {
       return   SqlDriver.getByJdbcTemplate(jdbcTemplate);
    }
}
