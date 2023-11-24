package run.strive.configcenter.refresh

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.concurrent.CustomizableThreadFactory
import run.strive.configcenter.configuration.MetaDataConfiguration
import run.strive.configcenter.exception.ConfigCenterServiceException
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

@Configuration
@ConditionalOnProperty(name = [MetaDataConfiguration.CONFIG_PREFIX + ".refresh.enable"], havingValue = "true")
class RefreshScheduleExcutor(private val metaDataConf: MetaDataConfiguration) : RefreshScheduleSupport(), InitializingBean {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RefreshScheduleExcutor::class.java)
    }

    private val scheduledExecutorService: ScheduledExecutorService =
        Executors.newSingleThreadScheduledExecutor(CustomizableThreadFactory("RefreshScheduleExcutor-"))

    override fun afterPropertiesSet() {
        LOGGER.info("schedule refresh config")
        scheduledExecutorService.scheduleAtFixedRate({
            try {
                doRefreshExcute()
            } catch (e: ConfigCenterServiceException) {
                LOGGER.error("refresh config error", e)
            }
        }, 10, metaDataConf.refresh.delay.toSeconds(), TimeUnit.SECONDS)
    }
}
