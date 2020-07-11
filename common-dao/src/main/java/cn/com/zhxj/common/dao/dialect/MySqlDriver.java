package cn.com.zhxj.common.dao.dialect;


import cn.com.zhxj.common.dao.core.DbGenValue;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class MySqlDriver extends SqlDriver  {

    protected MySqlDriver(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    protected SqlVisitor getExprVisitor() {
        return new MysqlVisitor();
    }

    @Override
    public String getDbGenerateSql(EntityDesc<?> entityDesc, FieldDesc fieldDesc, DbGenValue dbGenValue) {
        switch (dbGenValue){
            case CURRENT_DATETIME: return "NOW()";
            case CURRENT_TIMESTAMP: return "CURRENT_TIMESTAMP()";
            case CURRENT_DATETIME_UTC: return "UTC_TIMESTAMP()";
            case CURRENT_TIMESTAMP_UTC: return "UTC_TIMESTAMP()";

        }
        throw new IllegalArgumentException("不支持的DbGenValue："+dbGenValue);
    }
}
