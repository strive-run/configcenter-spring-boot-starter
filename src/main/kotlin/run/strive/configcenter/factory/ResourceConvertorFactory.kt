package run.strive.configcenter.factory

import run.strive.configcenter.convertor.PropertiesResourceConvertor
import run.strive.configcenter.convertor.ResourceConvertor
import run.strive.configcenter.convertor.YamlResourceConvertor
import run.strive.configcenter.exception.ConfigCenterServiceException

object ResourceConvertorFactory {
    private const val PROPERTIES_EXTENSION = ".properties"
    private val YAML_EXTENSION = arrayOf(".yml", ".yaml")

    fun getResourceConvertorInstance(fileName: String): ResourceConvertor {
        return if (fileName.endsWith(PROPERTIES_EXTENSION)) {
            PropertiesResourceConvertor()
        } else if (isYaml(fileName)) {
            YamlResourceConvertor()
        } else {
            throw ConfigCenterServiceException("配置中心：暂不支持的文件解析类型，文件名:$fileName")
        }
    }

    private fun isYaml(fileName: String): Boolean {
        for (extension in YAML_EXTENSION) {
            if (fileName.endsWith(extension)) {
                return true
            }
        }
        return false
    }
}
