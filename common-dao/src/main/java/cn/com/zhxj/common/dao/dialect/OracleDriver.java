package cn.com.zhxj.common.dao.dialect;

import cn.com.zhxj.common.dao.core.DbGenValue;
import cn.com.zhxj.common.dao.mapping.EntityDesc;
import cn.com.zhxj.common.dao.mapping.FieldDesc;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public class OracleDriver extends SqlDriver {


    protected OracleDriver(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        super(namedParameterJdbcTemplate);
    }

    @Override
    public String getDbGenerateSql(EntityDesc<?> entityDesc, FieldDesc fieldDesc, DbGenValue dbGenValue) {
        switch (dbGenValue){
            case CURRENT_DATETIME: return "SYSDATE";
            case CURRENT_DATETIME_UTC: return "SYS_EXTRACT_UTC(SYSTIMESTAMP)";
            case CURRENT_TIMESTAMP: return "CURRENT_TIMESTAMP";
            case CURRENT_TIMESTAMP_UTC: return "SYS_EXTRACT_UTC(SYSTIMESTAMP)";
        }
        throw new IllegalArgumentException("不支持的DbGenValue："+dbGenValue);
    }

    @Override
    protected SqlVisitor getExprVisitor() {
        return new OracleVisitor();
    }

}
