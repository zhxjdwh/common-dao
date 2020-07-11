package cn.com.zhxj.common.dao.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.invoke.SerializedLambda;
import java.util.List;

@Getter
@AllArgsConstructor
public class LambdaInfo {
    private SerializedLambda serializedLambda;
    private List<Class<?>>   parameterTypes;
}
