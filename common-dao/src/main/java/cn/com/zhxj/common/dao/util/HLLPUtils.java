package cn.com.zhxj.common.dao.util;

import com.clearspring.analytics.stream.cardinality.HyperLogLog;
import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import com.clearspring.analytics.stream.cardinality.ICardinality;
import com.clearspring.analytics.stream.cardinality.LinearCounting;

import java.util.List;
import java.util.function.Function;

public class HLLPUtils {
    public static <T> long estimate(List<T> list, Function<T,String> idFunc){
        ICardinality card = new HyperLogLog(16);
        for (T t : list) {
            String id = idFunc.apply(t);
            card.offer(id);
        }
        return card.cardinality();
    }


}
