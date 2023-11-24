package run.strive.configcenter.boot

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import run.strive.configcenter.configuration.MetaDataConfiguration

@Configuration
@EnableConfigurationProperties(MetaDataConfiguration::class)
class RemoteConfigAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    fun springValueAnnotationProcessor(): SpringValueAnnotationProcessor {
        return SpringValueAnnotationProcessor()
    }

    @Bean
    @ConditionalOnMissingBean
    fun refreshConfigExecutor(): RefreshConfigExecutor {
        return RefreshConfigExecutor()
    }
}
