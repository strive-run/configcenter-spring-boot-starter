package run.strive.configcenter.property

import java.lang.reflect.Field

data class SpringValueProperty(
    /**
     * @Value注解中的表达式
     */
    val express: String,

    /**
     * 表达式key
     */
    val key: String,

    /**
     * 当前配置值
     */
    var value: String,

    /**
     * 标记@Value的beanname
     */
    val beanName: String,

    /**
     * 标记@Value的bean
     */
    val bean: Any,

    /**
     * 标记@Value的字段
     */
    val field: Field,
)
