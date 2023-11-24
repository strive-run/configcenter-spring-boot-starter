package run.strive.configcenter.env

import org.springframework.lang.Nullable
import java.util.*
import java.util.stream.Collectors

class RemoteArgs(private val optionArgs: Properties) {

    val optionNames: Set<String>
        get() = Collections.unmodifiableSet(
            optionArgs.keys.stream().map { obj: Any -> obj.toString() }.collect(Collectors.toSet())
        )

    fun containsOption(optionName: String): Boolean {
        return optionArgs.containsKey(optionName)
    }

    @Nullable
    fun getOptionValue(optionName: String): Any? {
        return optionArgs[optionName]
    }
}
