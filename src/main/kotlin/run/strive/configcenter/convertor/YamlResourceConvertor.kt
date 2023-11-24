package run.strive.configcenter.convertor

import org.springframework.beans.factory.config.YamlProcessor
import org.springframework.core.CollectionFactory
import org.springframework.core.io.ByteArrayResource
import java.util.*

class YamlResourceConvertor : ResourceConvertor {

    override fun convertToProperties(content: String): Properties {
        val result = CollectionFactory.createStringAdaptingProperties()
        val yamlProcessor = object : YamlProcessor() {
            public override fun process(callback: MatchCallback) {
                super.process(callback)
            }
        }
        yamlProcessor.setResources(ByteArrayResource(content.toByteArray()))
        yamlProcessor.process { properties, _ -> result.putAll(properties) }
        return result
    }
}
