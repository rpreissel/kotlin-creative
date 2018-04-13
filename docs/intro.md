<br/>


## Kreativer Einsatz von Kotlin

Jax, 26.04.2018, Mainz

[@RenePreissel](https://twitter.com/RenePreissel)

<br/>
<br/>
<br/>

<small>https://github.com/rpreissel/kotlin-creative.git</small>

---

### Ktor - Asynchronous Server

```kotlin
@Location("/") class Index()
@Location("/hello/{name}") class Hello(val name: String)

get<Index> {
    call.respondHtml {
        body {
            h1 {
                +"Greeter"
            }
            div {
                a(locations.href(Hello("Rene"))) {
                    +"Say Hello"
                }
            }
        }
    }
}
```

---

### Anko - Android Libraries

```kotlin
override fun createView(ui: AnkoContext<Context>): View = with(ui) {
    constraintLayout {
        hello = textView("Hello World")

        applyConstraintSet {
            hello {
                connect(TOP to TOP of PARENT_ID margin dip(100),
                        LEFT to LEFT of PARENT_ID,
                        RIGHT to RIGHT of PARENT_ID,
                        BOTTOM to BOTTOM of PARENT_ID)
            }
        }
    }
}
```

---

### Inhalt

* Kotlin-Intro
* Kotlin-Syntax-Features mit Beispielen
    * KLogging, KotlinJS/React
    * Exposed, Gradle
    * Kodein, Anko, Ktor
    * ...

---

### Kotlin-Intro
```kotlin
class Circle(val radius: Double) {
    fun diameter(): Double {
        return radius * 2
    }
    val circumference: Double
        get() {
            return diameter() * PI
        }
    val area: Double
        get() = PI * radius * radius
}

fun main(args: Array<String>) {
    val circle = Circle(42.0)
    println(circle.radius)
    println("Diameter: ${circle.diameter()} Circumference: ${circle.circumference}")
}
```

<small class="fragment current-only" data-code-focus="1"></small>
<small class="fragment current-only" data-code-focus="14"></small>
<small class="fragment current-only" data-code-focus="15"></small>
<small class="fragment current-only" data-code-focus="2-4"></small>
<small class="fragment current-only" data-code-focus="5-8"></small>
<small class="fragment current-only" data-code-focus="9-10"></small>
<small class="fragment current-only" data-code-focus="16"></small>

---

### Kotlin-Logging mit Companion

```kotlin
class Circle(val radius: Double) {
    fun diameter(): Double {
        logger.info("compute diameter")
        return radius * 2
    }

    companion object : KLogging()
}

open class KLogging : KLoggable {
    override val logger: KLogger = logger()
}

fun main(args: Array<String>) : Unit {
    Circle.logger.info("companion access / e.g. static access")
}
```

<small class="fragment current-only" data-code-focus="3"></small>
<small class="fragment current-only" data-code-focus="7"></small>
<small class="fragment current-only" data-code-focus="10-12"></small>
<small class="fragment current-only" data-code-focus="15"></small>

---

### Lambdas

```kotlin
class Circle(val radius: Double) {
    val circumference: Double
        get() {
            logger.info {
                "compute circumference"
            }
            return diameter() * PI
        }

    companion object : KLogging()
}
```
```kotlin
fun info(msg: () -> Any?) {
    if (isInfoEnabled)
        info(msg().toString())
}
```

<small class="fragment current-only" data-code-focus="4-6"></small>
<small class="fragment current-only" data-code-focus="4, 12"></small>
<small class="fragment current-only" data-code-focus="14"></small>
