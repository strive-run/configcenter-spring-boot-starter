package run.strive.configcenter.boot

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.env.ConfigurableEnvironment
import run.strive.configcenter.factory.RemoteConfigProviderFactory

class RemoteConfigurationContextInitializer : RemoteConfigSupport(), ApplicationContextInitializer<ConfigurableApplicationContext>,
    EnvironmentPostProcessor, Ordered {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        initialize(environment)

    }

    override fun initialize(applicationContext: ConfigurableApplicationContext) {
        initialize(applicationContext.environment)
    }

    private fun initialize(environment: ConfigurableEnvironment) {
        // 防止重复初始化
        if (environment.propertySources.contains(BOOTSTRAP_PROPERTY_SOURCE_NAME)) {
            return
        }
        addPropertySource(environment, RemoteConfigProviderFactory(environment).properties)
    }

    override fun getOrder(): Int {
        return 0
    }
}

