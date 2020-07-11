package cn.com.zhxj.common.dao.config;

import cn.com.zhxj.common.dao.SqlDriverHolder;
import cn.com.zhxj.common.dao.core.Entity;
import cn.com.zhxj.common.dao.dialect.SqlCmdType;
import cn.com.zhxj.common.dao.dialect.SqlDriver;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class BeanConfig {

    @Bean
    public DataSource dataSource(){
        DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource("jdbc:oracle:thin:@xxxx:1521:xxxx","xxxx","xxxxx");
        return driverManagerDataSource;
    }


    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource){
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public SqlDriverHolder sqlDriverHolder(JdbcTemplate jdbcTemplate){
       return new SqlDriverHolder() {
           @Override
           public <T extends Entity> SqlDriver getSqlDriver(EntityDesc<T> entityDesc, SqlCmdType sqlType) {
              return SqlDriver.getByJdbcTemplate(jdbcTemplate);
           }
       };
    }


}
