package run.strive.configcenter.model

import java.util.*

class ConfigFileDO {
    /**
     * 文件唯一ID
     */
    lateinit var dataId: String

    /**
     * 环境
     */
    lateinit var profile: String

    /**
     * 后缀 properties / yaml / json
     */
    lateinit var extension: String

    /**
     * 内容
     */
    lateinit var content: String

    /**
     * 最后更新时间
     */
    lateinit var lastUpdatedTime: Date

    /**
     * 更新操作人
     */
    lateinit var lastUpdatedUser: String

    /**
     * 版本
     */
    var version: Int = 0
}
