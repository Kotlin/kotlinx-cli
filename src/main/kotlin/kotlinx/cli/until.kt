package kotlinx.cli

fun <T : Any> Iterator<T>.nextOrNull(): T? =
        if (hasNext()) next() else null