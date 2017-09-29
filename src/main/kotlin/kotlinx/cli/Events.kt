package kotlinx.cli

interface Event<out T> {
    fun addListener(listener: Listener<T>)
}

interface Listener<in T> {
    fun onEvent(value: T)
}

inline fun <T> Event<T>.add(crossinline listener: (T) -> Unit) {
    addListener(object : Listener<T> {
        override fun onEvent(value: T) {
            listener(value)
        }
    })
}

interface EventTrigger<in T> {
    fun trigger(value: T)
}

open class SimpleEvent<T> : Event<T>, EventTrigger<T> {
    private var listener: Listener<T>? = null

    override fun addListener(listener: Listener<T>) {
        if (this.listener == null) {
            this.listener = listener
        }
        else {
            throw IllegalStateException("SimpleEvent supports single listener only")
        }
    }

    override fun trigger(value: T) {
        listener?.onEvent(value)
    }
}


fun CommandLineInterface.onArgument(name: String, help: String, minArgs: Int = 0, maxArgs: Int = 1): Event<String> =
        SimpleEvent<String>().apply {
            positionalAction(name, help, minArgs, maxArgs) { trigger(it) }
        }

fun CommandLineInterface.onRemainingArguments(name: String, help: String): Event<String> =
        onArgument(name, help, minArgs = 0, maxArgs = Int.MAX_VALUE)

fun CommandLineInterface.onFlag(flag: String, help: String): Event<Nothing?> =
        onFlag(listOf(flag), help)

fun CommandLineInterface.onFlag(flags: List<String>, help: String): Event<Nothing?> =
        SimpleEvent<Nothing?>().apply {
            flagAction(flags, help) {
                trigger(null)
            }
        }

fun CommandLineInterface.onFlagValue(flag: String, valueSyntax: String, help: String): Event<String> =
        onFlagValue(listOf(flag), valueSyntax, help)

fun CommandLineInterface.onFlagValue(flags: List<String>, valueSyntax: String, help: String): Event<String> =
        SimpleEvent<String>().apply {
            flagValueAction(flags, valueSyntax, help) {
                trigger(it)
            }
        }

class MappedEvent<in T, R>(private val transformation: (T) -> R): SimpleEvent<R>(), Listener<T> {
    override fun onEvent(value: T) {
        trigger(transformation(value))
    }
}

fun <T, R> Event<T>.map(transformation: (T) -> R): Event<R> =
        MappedEvent(transformation).also {
            this@map.addListener(it)
        }

fun <T> Event<T>.onEach(action: (T) -> Unit) =
        apply { add(action) }

fun <T> Event<T>.once(action: (T) -> Unit) =
        apply {
            add {
                action(it)
                stopParsing()
            }
        }

class ArgumentStorage<T>(private var value: T): ArgumentValue<T> {
    override fun getValue(thisRef: Any?, prop: Any?): T =
            value

    fun setValue(newValue: T) {
        value = newValue
    }
}

fun <T> Event<T>.store(initialValue: T): ArgumentValue<T> =
        ArgumentStorage(initialValue).apply {
            add { setValue(it) }
        }

fun <T : Any> Event<T>.store(): ArgumentValue<T?> =
        store(null)

fun <T> Event<*>.storeConst(initialValue: T, storeValue: T): ArgumentValue<T> =
        ArgumentStorage(initialValue).apply {
            add { setValue(storeValue) }
        }

fun Event<*>.storeTrue() =
        storeConst(false, true)

fun <T> Event<T>.addTo(list: MutableList<T>): ArgumentValue<List<T>> =
        ArgumentStorage(list).apply {
            add { list.add(it) }
        }

fun <T> Event<T>.addToList() =
        addTo(ArrayList())
