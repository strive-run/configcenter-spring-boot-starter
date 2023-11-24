package run.strive.configcenter.boot

import org.springframework.beans.SimpleTypeConverter
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.BeanFactoryAware
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.Environment
import run.strive.configcenter.factory.RemoteConfigProviderFactory
import java.util.*

class RefreshConfigExecutor : RemoteConfigSupport(), BeanFactoryAware, EnvironmentAware {
    private lateinit var environment: ConfigurableEnvironment
    private lateinit var beanFactory: ConfigurableBeanFactory
    private lateinit var typeConverter: SimpleTypeConverter
    private lateinit var providerFactory: RemoteConfigProviderFactory

    fun execute() {
        val current = providerFactory.properties
        if (Optional.ofNullable(current).isPresent) {
            // 更新Environment
            addPropertySource(environment, current)
            // 更新bean实例的成员变量属性值
            updateField(beanFactory, environment, typeConverter, current)
        }
    }

    override fun setBeanFactory(beanFactory: BeanFactory) {
        this.beanFactory = beanFactory as ConfigurableBeanFactory
        typeConverter = SimpleTypeConverter().apply {
            conversionService = this@RefreshConfigExecutor.beanFactory.conversionService
        }
    }

    override fun setEnvironment(environment: Environment) {
        this.environment = environment as ConfigurableEnvironment
        providerFactory = RemoteConfigProviderFactory(this.environment)
    }
}
