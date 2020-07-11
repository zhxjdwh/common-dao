package cn.com.zhxj.common.dao.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple3<T1,T2,T3> extends Tuple {
    public Tuple3(){

    }
    public T1 f1;
    public T2 f2;
    public T3 f3;


    @Override
    public void setValue(int index, Object object) {
        if(object==null){
            return;
        }
        switch (index){
            case 0: f1=(T1)object;break;
            case 1: f2=(T2)object;break;
            case 2: f3=(T3)object;break;
            default: throw new IndexOutOfBoundsException("index out of bounds :"+index);
        }
    }
}
