package run.strive.configcenter.dao

import run.strive.configcenter.model.ConfigFileDO
import java.sql.ResultSet

class ApplicationConfigDaoImpl(private val dataAccessBase: DataAccessBase) : ApplicationConfigDao {

    companion object {
        private const val BASE_COLUMN = "data_id, profile, extension, content, last_updated_time, last_updated_user, version"
        const val DEFAULT_PROFILE = "local"

        private const val CONFIG_TABLE = "application_config_center_info"

        private const val SQL_SAVE_CONFIG_FILE =
            "insert into $CONFIG_TABLE ($BASE_COLUMN) values (?, ?, ?, ?, ?, ?, ?)"
        private const val SQL_FIND_CONFIG_FILE_LAST =
            "select $BASE_COLUMN from $CONFIG_TABLE where data_id = ? and profile = ? order by version desc limit 1"
        private const val SQL_FIND_CONFIG_FILE =
            "select $BASE_COLUMN from $CONFIG_TABLE where data_id = ? and profile = ? and version = ?"
        private const val SQL_LIST_CONFIG_FILE_HISTORY =
            "select $BASE_COLUMN from $CONFIG_TABLE where data_id = ? and profile = ?"
    }

    override fun saveConfigFile(configFileDO: ConfigFileDO) {
        val configFileLast = findConfigFileLast(configFileDO.dataId, configFileDO.profile)
        configFileDO.version = configFileLast?.let { configFileLast.version + 1 } ?: 1

        val parameters = arrayOf<Any>(
            configFileDO.dataId,
            configFileDO.profile,
            configFileDO.extension,
            configFileDO.content,
            configFileDO.lastUpdatedTime,
            configFileDO.lastUpdatedUser,
            configFileDO.version
        )

        dataAccessBase.getSqlPrepareStatement(SQL_SAVE_CONFIG_FILE, parameters).use {
            it.executeUpdate()
        }
    }

    override fun findConfigFileLast(fileId: String, profile: String): ConfigFileDO? {
        val parameters = arrayOf<Any>(fileId, profile)
        val queryExcuteResult = getQueryExcuteResult(SQL_FIND_CONFIG_FILE_LAST, parameters)
        return queryExcuteResult.stream().findFirst().orElse(null)
    }

    override fun findConfigFile(fileId: String, profile: String, version: Int): ConfigFileDO {
        val parameters = arrayOf<Any>(fileId, profile, version)
        val queryExcuteResult = getQueryExcuteResult(SQL_FIND_CONFIG_FILE, parameters)
        return queryExcuteResult.stream().findFirst().orElse(null)
    }

    override fun listConfigFileHistory(fileId: String, profile: String): List<ConfigFileDO> {
        return getQueryExcuteResult(SQL_LIST_CONFIG_FILE_HISTORY, arrayOf(fileId, profile))
    }

    private fun getQueryExcuteResult(sql: String, parameters: Array<Any>): List<ConfigFileDO> {
        return dataAccessBase.getSqlPrepareStatement(sql, parameters).use { ps ->
            ps.executeQuery().use { rs ->
                configFileDoListAssembler(rs)
            }
        }
    }

    private fun configFileDoListAssembler(resultSet: ResultSet): List<ConfigFileDO> {
        val configFileList: MutableList<ConfigFileDO> = mutableListOf()
        while (resultSet.next()) {
            val fileDO = ConfigFileDO()
            with(fileDO) {
                dataId = resultSet.getString(1)
                profile = resultSet.getString(2)
                extension = resultSet.getString(3)
                content = resultSet.getString(4)
                lastUpdatedTime = resultSet.getTimestamp(5)
                lastUpdatedUser = resultSet.getString(6)
                version = resultSet.getInt(7)
            }
            configFileList.add(fileDO)
        }
        return configFileList
    }
}
