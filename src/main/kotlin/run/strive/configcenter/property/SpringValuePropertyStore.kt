package run.strive.configcenter.property

import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.util.StringUtils
import java.util.*
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock

object SpringValuePropertyStore {
    private val LOCK = ReentrantReadWriteLock()
    private val READ_LOCK: Lock = LOCK.readLock()
    private val WRITE_LOCK: Lock = LOCK.writeLock()
    private var store: MultiValueMap<String, SpringValueProperty> = LinkedMultiValueMap()


    /**
     * 缓存@Value注解的元素
     * @param property
     */
    fun add(property: SpringValueProperty) {
        WRITE_LOCK.lock()
        try {
            store.add(property.key, property)
        } finally {
            WRITE_LOCK.unlock()
        }
    }

    /**
     * 判断配置是否发生变化
     * @param key           配置项key
     * @param currentValue  配置项当前value
     * @return
     */
    fun isChange(key: String, currentValue: String?): Boolean {
        return (StringUtils.hasLength(key)
                && StringUtils.hasLength(currentValue)
                && store.containsKey(key)
                && Objects.requireNonNull(store.getFirst(key)).value != currentValue)
    }

    /**
     * 更新所有Field当前属性值
     * @param key           配置项key
     * @param currentValue  配置项当前value
     */
    fun update(key: String, currentValue: String?) {
        WRITE_LOCK.lock()
        try {
            store[key]!!
                .stream().forEach { springValueProperty: SpringValueProperty -> springValueProperty.value = (currentValue!!) }
        } finally {
            WRITE_LOCK.unlock()
        }
    }

    /**
     * 根据配置项读取所有元素
     * @param key
     * @return
     */
    operator fun get(key: String): List<SpringValueProperty> {
        READ_LOCK.lock()
        return try {
            store[key]!!
        } finally {
            READ_LOCK.unlock()
        }
    }

}
