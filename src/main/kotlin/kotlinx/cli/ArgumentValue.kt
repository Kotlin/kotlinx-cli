package kotlinx.cli

interface ArgumentValue<out T> {
    operator fun getValue(thisRef: Any?, prop: Any?): T
}
