package cn.com.zhxj.common.dao.util;

import net.sf.cglib.beans.BeanMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class FastBeanUtils {
    public static Map toBeanMap(Object bean){
        return BeanMap.create(bean);
    }

    private static class EntityBeanMap implements Map{

        private BeanMap beanMap;

        @Override
        public int size() {
            return beanMap.size();
        }


        @Override
        public boolean isEmpty() {
            return beanMap.isEmpty();
        }


        @Override
        public boolean containsKey(Object key) {
            return beanMap.containsKey(key);
        }


        @Override
        public boolean containsValue(Object value) {
            return beanMap.containsValue(value);
        }


        @Override
        public Object get(Object key) {
            return beanMap.get(key);
        }


        @Override
        public Object put(Object key, Object value) {
            return null;
        }

        @Override
        public Object remove(Object key) {
            return null;
        }

        @Override
        public void putAll(Map m) {

        }

        @Override
        public void clear() {

        }

        @Override
        public Set keySet() {
            return null;
        }

        @Override
        public Collection values() {
            return null;
        }


        @Override
        public Set<Entry> entrySet() {
            return null;
        }

        @Override
        public boolean equals(Object o) {
            return false;
        }

        @Override
        public int hashCode() {
            return 0;
        }
    }
}
