package run.strive.configcenter.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

@ConfigurationProperties(prefix = MetaDataConfiguration.CONFIG_PREFIX)
class MetaDataConfiguration {
    companion object {
        const val CONFIG_PREFIX = "config-center"
    }

    var refresh: RefreshConfig = RefreshConfig()

    class DataBase {
        /**
         * 数据库链接
         */
        lateinit var url: String

        /**
         * DB username
         */
        lateinit var username: String

        /**
         * DB password
         */
        lateinit var password: String
    }


    class ConfigFile {
        /**
         * 配置文件ID
         */
        lateinit var id: String

        /**
         * 环境
         */
        lateinit var profile: String
    }

    class RefreshConfig {
        /**
         * 是否开启定时刷新
         */
        var enable = false

        /**
         * 刷新间隔
         */
        var delay: Duration = Duration.ofSeconds(30)
    }
}
