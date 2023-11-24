package run.strive.configcenter.provider

import org.springframework.core.env.ConfigurableEnvironment
import run.strive.configcenter.configuration.MetaDataConfiguration
import run.strive.configcenter.exception.ConfigCenterServiceException

class ConfigurableEnvironmentDecorator(private val environment: ConfigurableEnvironment) {
    val remoteConfigProvider: String
        get() = environment.getProperty(PROVIDER_KEY) ?: DataBaseConfigProvider::class.java.name

    fun loadDataBaseConfigBean(): DataBaseConfigBean {
        return DataBaseConfigBean(
            getPropertyAndCheck(DataBaseConfigBean.JDBC_URL_KEY),
            getPropertyAndCheck(DataBaseConfigBean.JDBC_USERNAME_KEY),
            getPropertyAndCheck(DataBaseConfigBean.JDBC_PASSW_KEY),
        )
    }

    fun loadConfigFileConfigBean(): ConfigFileConfigBean {
        return ConfigFileConfigBean(
            getPropertyAndCheck(ConfigFileConfigBean.CONFIG_DATA_ID),
            getPropertyAndCheck(ConfigFileConfigBean.CONFIG_DATA_PROFILE),
        )
    }

    private fun getPropertyAndCheck(configKey: String): String {
        val propertyValue = environment.getProperty(configKey)
        return propertyValue ?: throw ConfigCenterServiceException("获取指定配置信息失败，key=$configKey")
    }

    data class DataBaseConfigBean(val url: String, val userName: String, val password: String) {
        companion object {
            const val JDBC_URL_KEY = MetaDataConfiguration.CONFIG_PREFIX + ".data-base.url"
            const val JDBC_USERNAME_KEY = MetaDataConfiguration.CONFIG_PREFIX + ".data-base.username"
            const val JDBC_PASSW_KEY = MetaDataConfiguration.CONFIG_PREFIX + ".data-base.password"
        }
    }

    data class ConfigFileConfigBean(val dataId: String, val profile: String) {
        companion object {
            const val CONFIG_DATA_ID = MetaDataConfiguration.CONFIG_PREFIX + ".configfile.id"
            const val CONFIG_DATA_PROFILE = MetaDataConfiguration.CONFIG_PREFIX + ".configfile.profile"
        }
    }

    companion object {
        const val PROVIDER_KEY = MetaDataConfiguration.CONFIG_PREFIX + ".provider"
    }
}
