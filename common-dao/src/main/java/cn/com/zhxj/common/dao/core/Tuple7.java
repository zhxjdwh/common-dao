package cn.com.zhxj.common.dao.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple7<T1,T2,T3,T4,T5,T6,T7> extends Tuple {

    public Tuple7(){

    }
    public T1 f1;
    public T2 f2;
    public T3 f3;
    public T4 f4;
    public T5 f5;
    public T6 f6;
    public T7 f7;

    @Override
    public void setValue(int index, Object object) {
        if(object==null){
            return;
        }
        switch (index){
            case 0: f1=(T1)object;break;
            case 1: f2=(T2)object;break;
            case 2: f3=(T3)object;break;
            case 3: f4=(T4)object;break;
            case 4: f5=(T5)object;break;
            case 5: f6=(T6)object;break;
            case 6: f7=(T7)object;break;
            default: throw new IndexOutOfBoundsException("index out of bounds :"+index);
        }
    }
}
