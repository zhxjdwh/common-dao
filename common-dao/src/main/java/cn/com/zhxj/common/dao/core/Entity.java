package cn.com.zhxj.common.dao.core;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;

public interface Entity extends Serializable{

    default Entity copy() {
        return SerializationUtils.clone(this);
    }

}
