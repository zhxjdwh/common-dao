package cn.com.zhxj.common.dao.spring;

import cn.com.zhxj.common.dao.Dao;
import cn.com.zhxj.common.dao.impl.DefaultDaoImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.*;
import org.springframework.core.Ordered;
import org.springframework.core.ResolvableType;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;

import static org.springframework.beans.factory.support.AbstractBeanDefinition.*;


@Slf4j
public class DaoBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware, Ordered {


    private BeanFactory beanFactory;

    /**
     * Modify the application context's internal bean definition registry after its
     * standard initialization. All regular bean definitions will have been loaded,
     * but no beans will have been instantiated yet. This allows for adding further
     * bean definitions before the next post-processing phase kicks in.
     *
     * @param registry the bean definition registry used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        HashSet<String> entityClassNames=new HashSet<>();
        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            try {

                BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
                Class<?> beanClass = Class.forName(beanDefinition.getBeanClassName());
                ReflectionUtils.doWithLocalFields(beanClass, field -> {
                    Class<?> type = field.getType();
                    if (type.equals(Dao.class)) {
                        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                        Type entityType = genericType.getActualTypeArguments()[0];
                        Class entityClass = (Class) entityType;
                        if(entityClassNames.contains(entityClass.getName())){
                            return;
                        }
                        entityClassNames.add(entityClass.getName());
                        RootBeanDefinition beanDef = createBeanDef(entityClass);
                        registry.registerBeanDefinition("dao$" + entityClass.getName(), beanDef);
                    }
                });
            } catch (Throwable ignored) {

            }
        }
    }

    private RootBeanDefinition createBeanDef(Class<?> entityCls) {
        RootBeanDefinition beanDef = (RootBeanDefinition) BeanDefinitionBuilder.rootBeanDefinition(DefaultDaoImpl.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .setLazyInit(false)
                .setAbstract(false)
                .setDependencyCheck(AbstractBeanDefinition.DEPENDENCY_CHECK_ALL)
                .setAutowireMode(AUTOWIRE_BY_NAME)
                .getBeanDefinition();
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addIndexedArgumentValue(0,entityCls);
        beanDef.setConstructorArgumentValues(constructorArgumentValues);
        beanDef.setTargetType(ResolvableType.forClassWithGenerics(DefaultDaoImpl.class, entityCls));
        return beanDef;
    }

    /**
     * Modify the application context's internal bean factory after its standard
     * initialization. All bean definitions will have been loaded, but no beans
     * will have been instantiated yet. This allows for overriding or adding
     * properties even to eager-initializing beans.
     *
     * @param beanFactory the bean factory used by the application context
     * @throws BeansException in case of errors
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }

    /**
     * Callback that supplies the owning factory to a bean instance.
     * <p>Invoked after the population of normal bean properties
     * but before an initialization callback such as
     * {@link InitializingBean#afterPropertiesSet()} or a custom init-method.
     *
     * @param beanFactory owning BeanFactory (never {@code null}).
     *                    The bean can immediately call methods on the factory.
     * @throws BeansException in case of initialization errors
     * @see BeanInitializationException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }
}
