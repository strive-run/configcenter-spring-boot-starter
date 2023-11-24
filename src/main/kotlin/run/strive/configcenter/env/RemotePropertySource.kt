package run.strive.configcenter.env

import org.springframework.core.env.EnumerablePropertySource
import org.springframework.util.StringUtils

class RemotePropertySource(name: String, source: RemoteArgs) : EnumerablePropertySource<RemoteArgs>(name, source) {

    override fun getPropertyNames(): Array<String> {
        return StringUtils.toStringArray(source.optionNames)
    }

    override fun getProperty(name: String): Any? {
        return source.getOptionValue(name)
    }
}
