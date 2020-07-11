package cn.com.zhxj.common.dao.annotation;

import cn.com.zhxj.common.dao.handler.UtcDbTimeHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * insert time
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InsertTimeUtc {
    Class<UtcDbTimeHandler> insert = UtcDbTimeHandler.class;
}
