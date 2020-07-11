package cn.com.zhxj.common.dao.spring;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class CommonDaoSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        return new String[]{DaoBeanDefinitionRegistryPostProcessor.class.getName()};
    }
}
