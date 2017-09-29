package kotlinx.cli

class SharedOptionBuilder<T>(private val storage: ArgumentStorage<T>) {
    fun Event<T>.storeShared() {
        add { storage.setValue(it) }
    }

    fun Event<*>.storeShared(value: T) {
        add { storage.setValue(value) }
    }
}

inline fun <T> sharedOption(initialValue: T, build: SharedOptionBuilder<T>.() -> Unit): ArgumentValue<T> {
    val storage = ArgumentStorage(initialValue)
    val builder = SharedOptionBuilder(storage)
    builder.build()
    return storage
}