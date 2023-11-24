package run.strive.configcenter.boot

import org.slf4j.LoggerFactory
import org.springframework.beans.TypeConverter
import org.springframework.beans.factory.config.BeanExpressionContext
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.beans.factory.config.Scope
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.PropertySourcesPropertyResolver
import run.strive.configcenter.env.RemoteArgs
import run.strive.configcenter.env.RemotePropertySource
import run.strive.configcenter.property.SpringValuePropertyStore
import java.util.*
import java.util.stream.Collectors

abstract class RemoteConfigSupport {
    /**
     * 向环境变量中增加PropertySource
     *
     * @param environment
     * @param properties
     */
    protected fun addPropertySource(environment: ConfigurableEnvironment, properties: Properties) {
        val remoteArgs = RemoteArgs(properties)
        val remotePropertySource = RemotePropertySource(BOOTSTRAP_PROPERTY_SOURCE_NAME, remoteArgs)
        environment.propertySources.addFirst(remotePropertySource)
    }

    /**
     * 更新实例化Bean中的成员变量属性值
     *
     * @param beanFactory
     * @param environment
     * @param typeConverter 类型转换器，将配置值String转换为属性对应的类型值
     * @param properties    配置中心读取的最新配置项
     */
    protected fun updateField(
        beanFactory: ConfigurableBeanFactory,
        environment: ConfigurableEnvironment,
        typeConverter: TypeConverter,
        properties: Properties
    ) {
        // 找到发生变化的Z配置项
        val changed = findChanges(properties)
        if (changed.isEmpty()) {
            return
        }
        val resolver = PropertySourcesPropertyResolver(environment.propertySources)
        var scope: Scope?
        var typeValue: Any?

        // 遍历所有发生改变的配置项
        for (key in changed) {
            // 获取配置项对应的bean实例和field
            for (springValueProperty in SpringValuePropertyStore[key]) {
                // 获取bean的scope
                scope =
                    beanFactory.getRegisteredScope(beanFactory.getMergedBeanDefinition(springValueProperty.beanName).scope!!)
                // 解析@Value表达式，通过environment获取属性值，并根据SpEL表达式进行运算
                typeValue = beanFactory.beanExpressionResolver!!.evaluate(
                    resolver.resolvePlaceholders(springValueProperty.express),
                    BeanExpressionContext(beanFactory, scope)
                )
                springValueProperty.field.setAccessible(true)
                try {
                    // 根据Field类型进行转换
                    // 转换后赋值
                    springValueProperty.field[springValueProperty.bean] = typeConverter.convertIfNecessary(
                        typeValue,
                        springValueProperty.field.type,
                        TypeDescriptor(springValueProperty.field)
                    )
                } catch (e: IllegalAccessException) {
                    log.error("更新变量属性值发生错误", e)
                }
            }
            // 更新缓存
            SpringValuePropertyStore.update(key, properties.getProperty(key))
        }
        log.info("配置项已刷新")
    }

    private fun findChanges(properties: Properties): Set<String> {
        return properties.keys.stream()
            .map { obj: Any -> java.lang.String.valueOf(obj) }
            .filter { key: String -> SpringValuePropertyStore.isChange(key, properties.getProperty(key)) }
            .collect(Collectors.toSet())
    }

    companion object {
        private val log = LoggerFactory.getLogger(RemoteConfigSupport::class.java)
        const val BOOTSTRAP_PROPERTY_SOURCE_NAME = "remoteConfiguration"
    }
}
