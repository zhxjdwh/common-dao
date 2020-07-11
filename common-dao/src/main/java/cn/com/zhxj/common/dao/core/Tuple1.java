package cn.com.zhxj.common.dao.core;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Tuple1<T1> extends Tuple {
    public Tuple1(){

    }
    public T1 f1;

    @Override
    public void setValue(int index, Object object) {
        if(object==null){
            return;
        }
        switch (index){
            case 0: f1=(T1)object;
            default: throw new IndexOutOfBoundsException("index out of bounds :"+index);
        }
    }
}
