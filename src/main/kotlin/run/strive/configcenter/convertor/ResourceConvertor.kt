package run.strive.configcenter.convertor

import java.util.*

fun interface ResourceConvertor {
    fun convertToProperties(content: String): Properties
}
