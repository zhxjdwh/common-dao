package cn.com.zhxj.common.dao.annotation;

import cn.com.zhxj.common.dao.handler.IdUtilHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static cn.com.zhxj.common.dao.core.ExprFactory.$const;

/**
 * IdUtils
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface IdUtil {
    Class<IdUtilHandler> insert = IdUtilHandler.class;
}
