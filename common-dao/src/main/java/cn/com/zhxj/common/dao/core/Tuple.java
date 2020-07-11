package cn.com.zhxj.common.dao.core;

public abstract class Tuple {

    public static  <T1> Tuple1<T1> of(T1 t1){
        return new Tuple1<>(t1);
    }
    public static <T1,T2> Tuple2<T1,T2> of(T1 t1,T2 t2){
        return new Tuple2<>(t1,t2);
    }
    public static <T1,T2,T3> Tuple3<T1,T2,T3> of(T1 t1,T2 t2,T3 t3){
        return new Tuple3<>(t1,t2,t3);
    }
    public static <T1,T2,T3,T4> Tuple4<T1,T2,T3,T4> of(T1 t1,T2 t2,T3 t3,T4 t4){
        return new Tuple4<>(t1,t2,t3,t4);
    }
    public static <T1,T2,T3,T4,T5> Tuple5<T1,T2,T3,T4,T5> of(T1 t1,T2 t2,T3 t3,T4 t4,T5 t5){
        return new Tuple5<>(t1,t2,t3,t4,t5);
    }
    public static <T1,T2,T3,T4,T5,T6> Tuple6<T1,T2,T3,T4,T5,T6> of(T1 t1,T2 t2,T3 t3,T4 t4,T5 t5,T6 t6){
        return new Tuple6<>(t1,t2,t3,t4,t5,t6);
    }

    public static <T1,T2,T3,T4,T5,T6,T7> Tuple7<T1,T2,T3,T4,T5,T6,T7> of(T1 t1,T2 t2,T3 t3,T4 t4,T5 t5,T6 t6,T7 t7){
        return new Tuple7<>(t1,t2,t3,t4,t5,t6,t7);
    }

    public abstract void setValue(int index,Object object);
}
