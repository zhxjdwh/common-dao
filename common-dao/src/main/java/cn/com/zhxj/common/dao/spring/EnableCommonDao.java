package cn.com.zhxj.common.dao.spring;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({CommonDaoSelector.class})
public @interface EnableCommonDao {
    int order() default 2147483647;
}
