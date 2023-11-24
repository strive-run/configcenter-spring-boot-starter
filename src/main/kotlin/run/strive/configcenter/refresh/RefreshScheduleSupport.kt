package run.strive.configcenter.refresh

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.EnvironmentAware
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.springframework.util.StopWatch
import run.strive.configcenter.boot.RefreshConfigExecutor

@Component
class RefreshScheduleSupport : EnvironmentAware {
    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(RefreshScheduleSupport::class.java)
    }

    private lateinit var env: Environment

    @Autowired
    private lateinit var refreshConfigExecutor: RefreshConfigExecutor

    fun doRefreshExcute() {
        val stopWatch = StopWatch()
        refreshConfigExecutor.execute()
        LOGGER.info("refresh config duration: {} Ms", stopWatch.totalTimeMillis)
    }

    override fun setEnvironment(environment: Environment) {
        this.env = environment
    }
}
