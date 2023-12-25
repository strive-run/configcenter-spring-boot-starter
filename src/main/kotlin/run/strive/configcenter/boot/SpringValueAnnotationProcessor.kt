package run.strive.configcenter.boot

import org.springframework.beans.SimpleTypeConverter
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.BeanPostProcessor
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import org.springframework.core.env.PropertySourcesPropertyResolver
import org.springframework.util.ReflectionUtils
import run.strive.configcenter.property.SpringValueProperty
import run.strive.configcenter.property.SpringValuePropertyStore
import java.lang.reflect.Field
import java.util.*

class SpringValueAnnotationProcessor : BeanPostProcessor, BeanFactoryAware, EnvironmentAware {
    private lateinit var environment: ConfigurableEnvironment
    private lateinit var beanFactory: ConfigurableBeanFactory
    private var typeConverter: SimpleTypeConverter? = null

    override fun postProcessBeforeInitialization(bean: Any, beanName: String): Any {
        val resolver = PropertySourcesPropertyResolver(
            environment.propertySources
        )
        ReflectionUtils.doWithFields(bean.javaClass) { field: Field ->
            val value = field.getAnnotation(
                Value::class.java
            )
            // 缓存@Value标记的Field
            if (Optional.ofNullable(value).isPresent) {
                // 获取配置项Key
                val key = resolver.resolvePlaceholders(value.value);
                SpringValuePropertyStore.add(
                    SpringValueProperty(
                        value.value,
                        key,
                        environment.getProperty(key),
                        beanName,
                        bean,
                        field
                    )
                )
            }
        }
        return bean
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableBeanFactory
        typeConverter = SimpleTypeConverter()
            .apply { conversionService = this@SpringValueAnnotationProcessor.beanFactory.conversionService }
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment as ConfigurableEnvironment
    }
}
