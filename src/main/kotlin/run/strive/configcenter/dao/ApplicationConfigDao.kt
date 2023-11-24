package run.strive.configcenter.dao

import run.strive.configcenter.model.ConfigFileDO

interface ApplicationConfigDao {
    /**
     * 新增配置
     * @param configFileDO
     */
    fun saveConfigFile(configFileDO: ConfigFileDO)

    /**
     * 获取配置最新版本
     * @param fileId ID
     * @param profile 环境
     */
    fun findConfigFileLast(fileId: String, profile: String = ApplicationConfigDaoImpl.DEFAULT_PROFILE): ConfigFileDO?

    /**
     * 获取配置指定版本
     * @param fileId ID
     * @param profile 环境
     * @param version 版本
     * @return 指定版本的配置内容
     */
    fun findConfigFile(fileId: String, profile: String = ApplicationConfigDaoImpl.DEFAULT_PROFILE, version: Int): ConfigFileDO?

    /**
     * 获取配置历史版本列表
     * @param fileId ID
     * @param profile 环境
     */
    fun listConfigFileHistory(fileId: String, profile: String = ApplicationConfigDaoImpl.DEFAULT_PROFILE): List<ConfigFileDO>?
}
