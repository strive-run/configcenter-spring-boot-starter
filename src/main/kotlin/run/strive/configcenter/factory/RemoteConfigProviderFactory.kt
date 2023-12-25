package run.strive.configcenter.factory

import org.springframework.core.env.ConfigurableEnvironment
import run.strive.configcenter.exception.ConfigCenterServiceException
import run.strive.configcenter.provider.ConfigurableEnvironmentDecorator
import run.strive.configcenter.provider.DataBaseConfigProvider
import run.strive.configcenter.provider.RemoteConfigProvider
import java.util.*

class RemoteConfigProviderFactory(environment: ConfigurableEnvironment) {
    private val configurableEnvironmentDecorator: ConfigurableEnvironmentDecorator =
        ConfigurableEnvironmentDecorator(environment)

    val properties: Properties
        get() {
            val remoteConfigProvider = configurableEnvironmentDecorator.remoteConfigProvider
            return if (remoteConfigProvider == DataBaseConfigProvider::class.java.name) {
                val dataBaseConfigBean = configurableEnvironmentDecorator.loadDataBaseConfigBean()
                val configFileConfigBean = configurableEnvironmentDecorator.loadConfigFileConfigBean()
                DataBaseConfigProvider(dataBaseConfigBean, configFileConfigBean).load()
            } else {
                loadProvider()
            }
        }

    private fun loadProvider(): Properties {
        try {
            val clazz = Thread.currentThread().getContextClassLoader().loadClass(configurableEnvironmentDecorator.remoteConfigProvider)
            val provider = clazz.getDeclaredConstructor().newInstance() as RemoteConfigProvider
            return provider.load()
        } catch (e: Throwable) {
            System.err.printf("加载自定义配置类发生错误[%s]%n", configurableEnvironmentDecorator.remoteConfigProvider)
            throw ConfigCenterServiceException(e)
        }
    }
}
