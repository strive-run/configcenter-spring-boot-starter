package run.strive.configcenter.provider

import org.slf4j.LoggerFactory
import org.springframework.util.StringUtils
import run.strive.configcenter.dao.ApplicationConfigDao
import run.strive.configcenter.dao.ApplicationConfigDaoImpl
import run.strive.configcenter.dao.DataAccessBase
import run.strive.configcenter.exception.ConfigCenterServiceException
import run.strive.configcenter.factory.ResourceConvertorFactory
import run.strive.configcenter.model.ConfigFileDO
import run.strive.configcenter.model.ConfigFileStore

import java.util.*

class DataBaseConfigProvider(
    private val dataBaseConfigBean: ConfigurableEnvironmentDecorator.DataBaseConfigBean,
    private val configFileConfigBean: ConfigurableEnvironmentDecorator.ConfigFileConfigBean
) : RemoteConfigProvider {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(DataBaseConfigProvider::class.java)
    }

    override fun load(): Properties {
        val dataAccessBase = DataAccessBase(
            dataBaseConfigBean.url,
            dataBaseConfigBean.userName,
            dataBaseConfigBean.password
        )
        val applicationConfigDao: ApplicationConfigDao = ApplicationConfigDaoImpl(dataAccessBase)
        val configFileLast: ConfigFileDO =
            applicationConfigDao.findConfigFileLast(configFileConfigBean.dataId, configFileConfigBean.profile)
                ?: throw ConfigCenterServiceException(
                    String.format(
                        "load config file [dataId=%s, profile=%s] from db error",
                        configFileConfigBean.dataId,
                        configFileConfigBean.profile
                    )
                )
        if (!checkAndGetIncrementConfig(configFileLast)) {
            LOGGER.warn("config is not changed, method return ...")
            return Properties()
        }
        val content: String = configFileLast.content
        if (!StringUtils.hasLength(content)) {
            LOGGER.warn("load config file from db error, contenr is empty, content:{}", content)
            return Properties()
        }
        val fileName = String.format("%s-%s.%s", configFileLast.profile, configFileLast.dataId, configFileLast.extension)
        return ResourceConvertorFactory.getResourceConvertorInstance(fileName).convertToProperties(content)
    }

    /**
     * 基于版本以及最后更新时间比对，检查配置是否有变更
     * @param currentConfigFile 当前获取的最新配置值
     * @return 增量配置文件或者null
     */
    private fun checkAndGetIncrementConfig(currentConfigFile: ConfigFileDO): Boolean {
        val localConfigStoreGroupMap: Map<String, ConfigFileDO> = ConfigFileStore.configGroupMap
        return localConfigStoreGroupMap[ConfigFileStore.DEFAULT_GROUP_NAME]?.let {
            currentConfigFile.version > it.version
        } ?: true
    }
}
