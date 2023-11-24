package run.strive.configcenter.convertor

import java.io.StringReader
import java.util.*

class PropertiesResourceConvertor : ResourceConvertor {
    override fun convertToProperties(content: String): Properties {
        val properties = Properties()
        properties.load(StringReader(content))

        return properties
    }
}
