package cn.com.zhxj.common.dao.annotation;

import cn.com.zhxj.common.dao.handler.DbTimeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库当前时间
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdateTime {
    Class<DbTimeHandler> insert = DbTimeHandler.class;
    Class<DbTimeHandler> update = DbTimeHandler.class;
}
