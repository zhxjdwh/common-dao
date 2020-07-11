package cn.com.zhxj.common.dao.impl;

import cn.com.zhxj.common.dao.util.PropertyCall;

import java.util.List;
import java.util.stream.Collectors;

public class PropertyCallIterator {

    private int                setterIndex=0;
    private int                getterIndex=0;
    private List<PropertyCall> callList;

    public PropertyCallIterator(List<PropertyCall> callList) {
        this.callList = callList;
    }

    public boolean hasMoreSetter(){
        int idx = this.setterIndex;
        for (; idx < callList.size() ;idx++) {
            PropertyCall call = callList.get(idx);
            if(call.isSetter()) return true;
        }
        return false;
    }

    public PropertyCall nextSetter(){
        for (; this.setterIndex < callList.size() ;) {
            PropertyCall call = callList.get(this.setterIndex);
            this.setterIndex++;
            if(call.isSetter()) return call;
        }
        return null;
    }

    public boolean hasMoreGetter(){
        int idx = this.getterIndex;
        for (; idx< callList.size() ;idx++) {
            PropertyCall call = callList.get(idx);
            if(call.isGetter()) return true;
        }
        return false;
    }

    public PropertyCall nextGetter(){
        for (; getterIndex < callList.size() ;) {
            PropertyCall call = callList.get(getterIndex);
            getterIndex++;
            if(call.isGetter()) return call;
        }
        return null;
    }

    public List<PropertyCall> allGetter(){
       return callList.stream().filter(PropertyCall::isGetter).collect(Collectors.toList());
    }
    public List<PropertyCall> allSetter(){
        return callList.stream().filter(PropertyCall::isSetter).collect(Collectors.toList());
    }
}
