## 0.3.6 ##

* Support using `parse(args)` in `fun main(vararg args: String)` ([GH-95](https://github.com/Kotlin/kotlinx-cli/pull/95))
* Update Kotlin to 1.9.10 ([GH-102](https://github.com/Kotlin/kotlinx-cli/pull/102))

## 0.3.5 ##

* Fix typo in GNU option message ([GH-75](https://github.com/Kotlin/kotlinx-cli/pull/75))
* Fix Choice's default toVariant ([GH-77](https://github.com/Kotlin/kotlinx-cli/pull/77))
* Enable HMPP with compatibility metadata variant ([GH-83](https://github.com/Kotlin/kotlinx-cli/pull/83))

## 0.3.4 ##

* Support MacOSArm64 target ([GH-73](https://github.com/Kotlin/kotlinx-cli/pull/73))
* Support error handling in custom ArgTypes ([GH-72](https://github.com/Kotlin/kotlinx-cli/pull/72))

## 0.3.3 ##

* Fix to report correct exit code in case of non-parsable input ([GH-66](https://github.com/Kotlin/kotlinx-cli/pull/66))

## 0.3.2 ##

* Added option to strict order of subcommands and their parameters([GH-55](https://github.com/Kotlin/kotlinx-cli/pull/55))
* Removed stack trace from error message with full usage info([GH-58](https://github.com/Kotlin/kotlinx-cli/pull/58))

## 0.3.1 ##

* Fix to get full help information for subcommands ([GH-44](https://github.com/Kotlin/kotlinx-cli/pull/44))

## 0.3 ##
Library version compatible with Kotlin 1.4.0

* [GNU options style](https://www.gnu.org/software/libc/manual/html_node/Argument-Syntax.html) support ([#27](https://github.com/Kotlin/kotlinx-cli/issues/27))
* Flexible order of base command options and subcommands ones ([GH-33](https://github.com/Kotlin/kotlinx-cli/pull/33))
* Enumeration support for `ArgType.Choice` ([GH-35](https://github.com/Kotlin/kotlinx-cli/pull/35))

## 0.2.1 ##
Library version compatible with Kotlin 1.3.70

## 0.2 ##
Complete rewrite of a generic command-line parser (compatible with Kotlin 1.3.61)
