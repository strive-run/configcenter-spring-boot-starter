package run.strive.configcenter.provider

import java.util.*

fun interface RemoteConfigProvider {
    fun load(): Properties
}
