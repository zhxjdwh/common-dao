package cn.com.zhxj.common.dao.core;

import org.apache.commons.lang3.StringUtils;

public enum ExamplePolicy {
    /**
     * 用于Update
     * 默认值,只包含not null值,null值字段会被忽略
     */
    INCLUDE_NOT_NULL(0),
    /**
     * 用于Update/查询
     * 包含null,对于null值会翻译成 is null
     */
    INCLUDE_NULL(1),
    /**
     * 用于查询条件
     * 排除掉 null值和String empty
     */
    EXCLUDE_NULL_EMPTY(2),
    /**
     * 用于查询条件
     * 排除掉 null值和String empty,String Blank
     */
    EXCLUDE_NULL_EMPTY_BLANK(3);

    private int type;

    ExamplePolicy(int type) {
        this.type = type;
    }


    public boolean canAccept(Object val) {
        if (type == 1) return true; //INCLUDE_NULL
        switch (type) {
            case 0:
                return val != null;
            case 2: {
                if (val == null) return false;
                if (val.getClass()!=String.class) return true;
                return StringUtils.isNotEmpty((String) val);
            }
            case 3:{
                if (val == null) return false;
                if (val.getClass()!=String.class) return true;
                return StringUtils.isNotBlank((String) val);
            }
        }
        throw new RuntimeException("ExamplePolicy代码有问题???????");
    }


}
