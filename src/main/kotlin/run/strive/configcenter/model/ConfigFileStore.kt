package run.strive.configcenter.model

object ConfigFileStore {
    const val DEFAULT_GROUP_NAME: String = "default"

    val configGroupMap: Map<String, ConfigFileDO> = mutableMapOf()
}
