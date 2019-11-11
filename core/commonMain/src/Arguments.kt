/*
 * Copyright 2010-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */
package kotlinx.cli

import kotlin.reflect.KProperty

internal data class CLIEntityWrapper(var entity: CLIEntity<*>? = null)

/**
 * The base class for a command line argument or an option.
 */
abstract class CLIEntity<TResult> internal constructor(internal val owner: CLIEntityWrapper) {
    /**
     * The delegate object returned by [provideDelegate] operator call.
     */
    // TODO: Initialized only from constructors and never changed after
    //       remove lateinit, make val
    lateinit var delegate: ArgumentValueDelegate<TResult>
        internal set

    /**
     * The value of the option or argument parsed from command line.
     *
     * Accessing this property before it gets its value will result in an exception.
     * You can use [valueOrigin] property to find out whether the property has been already set.
     *
     * @see ArgumentValueDelegate.value
     */
    // TODO: decide on the exception type
    // TODO: decide whether 'set' is possible before calling 'parse'
    var value: TResult
        get() = delegate.value
        set(value) { delegate.value = value }

    /**
     * The origin of the option/argument value.
     */
    val valueOrigin: ArgParser.ValueOrigin
        get() = (delegate as ParsingValue<*, *>).valueOrigin

    /**
     * Returns the delegate object for property delegation and initializes it with the name of the delegated property.
     *
     * This operator makes it possible to delegate a property to this instance. It returns [delegate] object
     * to be used as an actual delegate and uses the name of the delegated property to initialize the full name
     * of the option/argument if it wasn't done during construction of that option/argument.
     */
    // TODO: should it be a single-shot method?
    operator fun provideDelegate(thisRef: Any?, prop: KProperty<*>): ArgumentValueDelegate<TResult> {
        (delegate as ParsingValue<*, *>).provideName(prop.name)
        return delegate
    }
}

/**
 * The base class for command line arguments.
 *
 * You can use [ArgParser.argument] function to declare an argument.
 */
abstract class Argument<TResult> internal constructor(owner: CLIEntityWrapper): CLIEntity<TResult>(owner)

/**
 * The base class of an argument with a single value.
 *
 * A non-optional argument or an optional argument with a default value is represented with the [SingleArgument] inheritor.
 * An optional argument having nullable value is represented with the [SingleNullableArgument] inheritor.
 */
// TODO: investigate if we can collapse two inheritors into the single base class and specialize extensions by TResult upper bound
abstract class AbstractSingleArgument<T: Any, TResult> internal constructor(owner: CLIEntityWrapper): Argument<TResult>(owner) {
    /**
     * Check descriptor for this kind of argument.
     */
    internal fun checkDescriptor(descriptor: ArgDescriptor<*, *>) {
        if (descriptor.number == null || descriptor.number > 1) {
            failAssertion("Argument with single value can't be initialized with descriptor for multiple values.")
        }
    }
}

/**
 * A non-optional argument or an optional argument with a default value.
 *
 * The [value] of such argument is non-null.
 */
class SingleArgument<T : Any> internal constructor(descriptor: ArgDescriptor<T, T>, owner: CLIEntityWrapper):
        AbstractSingleArgument<T, T>(owner) {
    init {
        checkDescriptor(descriptor)
        delegate = ArgumentSingleValue(descriptor)
    }
}

/**
 * An optional argument with nullable [value].
 */
class SingleNullableArgument<T : Any> internal constructor(descriptor: ArgDescriptor<T, T>, owner: CLIEntityWrapper):
        AbstractSingleArgument<T, T?>(owner){
    init {
        checkDescriptor(descriptor)
        delegate = ArgumentSingleNullableValue(descriptor)
    }
}

/**
 * An argument that allows several values to be provided in command line string.
 *
 * The [value] property of such argument has type `List<T>`.
 */
class MultipleArgument<T : Any> internal constructor(descriptor: ArgDescriptor<T, List<T>>, owner: CLIEntityWrapper):
        Argument<List<T>>(owner) {
    init {
        if (descriptor.number != null && descriptor.number < 2) {
            failAssertion("Argument with multiple values can't be initialized with descriptor for single one.")
        }
        delegate = ArgumentMultipleValues(descriptor)
    }
}

/**
 * Allows the argument to have several values specified in command line string.
 *
 * @param value the exact number of values expected for this argument, but at least 2.
 */
// TODO: rename the parameter to 'number'?
fun <T : Any, TResult> AbstractSingleArgument<T, TResult>.multiple(value: Int): MultipleArgument<T> {
    if (value < 2) {
        // TODO: use `require` to throw an IllegalArgumentException
        error("multiple() modifier with value less than 2 is unavailable. It's already set to 1.")
    }
    val newArgument = with((delegate as ParsingValue<T, T>).descriptor as ArgDescriptor) {
        MultipleArgument(ArgDescriptor(type, fullName, value, description, listOfNotNull(defaultValue),
                required, deprecatedWarning), owner)
    }
    owner.entity = newArgument
    return newArgument
}

/**
 * Allows the last argument to take all the trailing values in command line string.
 */
fun <T : Any, TResult> AbstractSingleArgument<T, TResult>.vararg(): MultipleArgument<T> {
    val newArgument = with((delegate as ParsingValue<T, T>).descriptor as ArgDescriptor) {
        MultipleArgument(ArgDescriptor(type, fullName, null, description, listOfNotNull(defaultValue),
                required, deprecatedWarning), owner)
    }
    owner.entity = newArgument
    return newArgument
}

/**
 * Specifies the default value for the argument, that will be used when no value is provided for the argument
 * in command line string.
 *
 * @param value the default value.
 */
fun <T: Any, TResult> AbstractSingleArgument<T, TResult>.default(value: T): SingleArgument<T> {
    val newArgument = with((delegate as ParsingValue<T, T>).descriptor as ArgDescriptor) {
        SingleArgument(ArgDescriptor(type, fullName, number, description, value, required, deprecatedWarning), owner)
    }
    owner.entity = newArgument
    return newArgument
}

/**
 * Specifies the default value for the argument with multiple values, that will be used when no values are provided
 * for the argument in command line string.
 *
 * @param value the default value, must be a non-empty collection.
 */
fun <T: Any> MultipleArgument<T>.default(value: Collection<T>): MultipleArgument<T> {
    if (value.isEmpty()) {
        // TODO: use `require` to throw an IllegalArgumentException
        error("Default value for argument can't be empty collection.")
    }
    val newArgument = with((delegate as ParsingValue<T, List<T>>).descriptor as ArgDescriptor) {
        MultipleArgument(ArgDescriptor(type, fullName, number, description, value.toList(),
                required, deprecatedWarning), owner)
    }
    owner.entity = newArgument
    return newArgument
}

/**
 * Allows the argument to have no value specified in command line string.
 *
 * The value of the argument is `null` in case if no value was specified in command line string.
 *
 * Note that only trailing arguments can be optional, i.e. no required arguments can follow optional ones.
 */
// TODO: decide the behavior of argument().default().optional()
fun <T: Any> SingleArgument<T>.optional(): SingleNullableArgument<T> {
    val newArgument = with((delegate as ParsingValue<T, T>).descriptor as ArgDescriptor) {
        SingleNullableArgument(ArgDescriptor(type, fullName, number, description, defaultValue,
                false, deprecatedWarning), owner)
    }
    owner.entity = newArgument
    return newArgument
}

/**
 * Allows the argument with multiple values to have no values specified in command line string.
 *
 * The value of the argument is an empty list in case if no value was specified in command line string.
 *
 * Note that only trailing arguments can be optional: no required arguments can follow the optional ones.
 */
fun <T: Any> MultipleArgument<T>.optional(): MultipleArgument<T> {
    val newArgument = with((delegate as ParsingValue<T, List<T>>).descriptor as ArgDescriptor) {
        MultipleArgument(ArgDescriptor(type, fullName, number, description,
                defaultValue?.toList() ?: listOf(), false, deprecatedWarning), owner)
    }
    owner.entity = newArgument
    return newArgument
}

// TODO: internal
fun failAssertion(message: String): Nothing = throw AssertionError(message)