### Tracing

```kotlin
class Circle(val radius: Double) {
    fun diameter(): Double
        = radius * 2
}
```
<div class="fragment" data-fragment-index="2">
```kotlin
class Circle(val radius: Double) {
    fun diameter(): Double = try {
        logger.info("diameter - begin")
        radius * 2
    } finally {
        logger.info("diameter - end")
    }

    companion object : KLogging()
}
```
</div>
<small class="fragment current-only" data-code-focus="2-3" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="6-11" data-fragment-index="3"></small>
<small class="fragment current-only" data-code-focus="7,10" data-fragment-index="4"></small>


---

### Tracing mit Lambdas

```kotlin
class Circle(val radius: Double) {
    fun diameter(): Double = withTracing("diameter") {
        radius * 2
    }
    companion object : MyLogging()
}
```
<div class="fragment" data-fragment-index="2">
```kotlin
open class MyLogging : KLogging() {
    fun <T> withTracing(
        prefix: String, block: () -> T
    ) = try {
        logger.info("$prefix - begin")
        block()
    } finally {
        logger.info("$prefix - end")
    }
}
```
</div>
<small class="fragment current-only" data-code-focus="2-4" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="5,7" data-fragment-index="3"></small>
<small class="fragment current-only" data-code-focus="8" data-fragment-index="4"></small>
<small class="fragment current-only" data-code-focus="9" data-fragment-index="5"></small>
<small class="fragment current-only" data-code-focus="10-15" data-fragment-index="6"></small>
<small class="fragment current-only" data-code-focus="12" data-fragment-index="7"></small>
<small class="fragment current-only" data-code-focus="2-4" data-fragment-index="8"></small>

---

### Tracing innerhalb von Funktionen

```kotlin
class Circle(val radius: Double) {
    val area: Double
        get() = withTracing("area") {
            logger.info("area - before radius square")
            val r2 = radius * radius
            logger.info("area - after radius square")
            PI * r2
        }

    companion object : MyLogging()
}
```

<small class="fragment current-only" data-code-focus="4,6"></small>


---

### Lambda mit Kontext

```kotlin
class Circle(val radius: Double) {
    val area: Double
        get() = withTracing("area") {
            trace("before radius square")
            val r2 = radius * radius
            trace("after radius square")
            PI * r2
        }
}
```
<div class="fragment" data-fragment-index="2">
```kotlin
class TracingContext(val logger: KLogger, val prefix: String) {
    fun trace(msg: String) {
        logger.info("$prefix - $msg")
    }
}
```
</div>
<small class="fragment current-only" data-code-focus="4,6" data-fragment-index="1">In Java: ```tracer.trace()``` oder mittels ```ThreadLocal ```</small>
<small class="fragment current-only" data-code-focus="10" data-fragment-index="3"></small>
<small class="fragment current-only" data-code-focus="4,6,11-13" data-fragment-index="4">```this```-Zeiger wird zu ```TracingContext```</small>

---

### Lambda with Receiver - Lambda++

```kotlin
open class MyLogging : KLogging() {
    fun <T> withTracing(
        prefix: String, block: TracingContext.() -> T
    ): T = try {
        logger.info("$prefix - begin")
        val context = TracingContext(logger, prefix)
        context.block()
    } finally {
        logger.info("$prefix - end")
    }
}
```
<div class="fragment" data-fragment-index="4">
```kotlin
val area: Double
    get() = withTracing("area") { // 'this' ist ein TracingContext
        trace("before radius square")
        ...
```
</div>
<small class="fragment current-only" data-code-focus="3" data-fragment-index="1">```TracingContext``` vor dem ```()``` definiert den Kontext</small>
<small class="fragment current-only" data-code-focus="6" data-fragment-index="2"></small>
<small class="fragment current-only" data-code-focus="7" data-fragment-index="3">Der Block kann nur mit einem ```TracingContext``` aufgerufen werden</small>
<small class="fragment current-only" data-code-focus="13" data-fragment-index="5"></small>
<small class="fragment current-only" data-code-focus="14" data-fragment-index="6"></small>

Note:
14min

---

### KotlinJS / React

* KotlinJS transpiliert nach JavaScript
* Wrapper für React ist vorhanden
* Interne DSL für HTML (ähnlich JSX) vorhanden

---

### Lambda++ in KotlinJS / React

```kotlin
div("App-header") {
    key = "header"
    logo()
    h2 {
        +"Welcome to React with Kotlin"
    }
}
p("App-intro") {
    key = "intro"
    +"To get started, edit "
    code { +"app/App.kt" }
    +" and save to reload."
}
```
<small class="fragment current-only" data-code-focus="1-7">```this``` ist ein ```RDomBuilder```</small>
<small class="fragment current-only" data-code-focus="4-6"></small>
<small class="fragment current-only" data-code-focus="11"></small>

---

### Lambda++

* Expliziter Kontext (```this```) für einen Code-Block
* Nutzen:
    * Bereitstellen von speziellen Funktionen im Code-Block
    * Vermeidung von wiederholenden expliziten Parametern
    * Umsetzung eines Builder-Patterns - Zusammensetzen von komplexen Objekten

Note:
16min


---

### Extension-Funktionen

```kotlin
val url: URL = StringUtil.toURL("http://localhost:8080")

object StringUtil {
    fun toURL(s: String): URL = URL(s)
}
```
<br/>
<div class="fragment" data-fragment-index="3">
```kotlin
fun String.toURL(): URL = URL(this)

val url = "http://localhost:8080".toURL()
```
</div>
<small class="fragment current-only" data-code-focus="1" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="3-5" data-fragment-index="2"></small>
<small class="fragment current-only" data-code-focus="6" data-fragment-index="4"></small>
<small class="fragment current-only" data-code-focus="8" data-fragment-index="5"></small>

---

### Extension anstelle von Vererbung

```kotlin
open class MyLogging : KLogging() {
    fun <T> withTracing(
        prefix: String,
        block: TracingContext.() -> T
    ): T = ...
}

class Circle(val radius: Double) {
    fun diameter(): Double = withTracing("diameter") {
        radius * 2
    }

    companion object : MyLogging()
}
```
<small class="fragment current-only" data-code-focus="1"></small>
<small class="fragment current-only" data-code-focus="13"></small>

---

### Extension anstelle von Vererbung

```kotlin
fun <T> KLogging.withTracing(
    prefix: String,
    block: TracingContext.() -> T
): T = ...

class Circle(val radius: Double) {
    fun diameter(): Double = withTracing("diameter") {
        radius * 2
    }

    companion object : KLogging()
}
```
<small class="fragment current-only" data-code-focus="1"></small>
<small class="fragment current-only" data-code-focus="11"></small>
<small class="fragment current-only" data-code-focus="7"></small>

---

### Extensions in KotlinJS/React

```kotlin
div("App-header") {
    key = "header"
    logo()
    h2 {
        +"Welcome to React with Kotlin"
    }
}
```
<div class="fragment" data-fragment-index="3">
```kotlin
fun RBuilder.logo(height: Int = 100) {
    div("Logo") {
        attrs.style = js {
            this.height = height
        }
        img(alt = "React logo.logo", src = reactLogo, classes = "Logo-react") {}
    }
}
```
</div>
<small class="fragment current-only" data-code-focus="1-7" data-fragment-index="1">Funktionen in der Klasse ```RBuilder```</small>
<small class="fragment current-only" data-code-focus="3" data-fragment-index="2">Extensions</small>
<small class="fragment current-only" data-code-focus="8" data-fragment-index="4">Extension-Funktion der Klasse ```RBuilder``` erweitert die DSL</small>
<small class="fragment current-only" data-code-focus="9" data-fragment-index="5">Hat selber Zugriff auf die anderen Funktionen der DSL</small>

---

### Extension-Funktionen

* Zusätzliche Funktionen an vorhandenen Typen
* Nutzen:
    * Vermeidung von Util-Klassen
    * Vermeidung von Vererbung
    * Umsetzung von erweiterbareren DSLs

Note:
21min

---

### Lokale Extension-Funktionen

```kotlin
val area: Double
    get() = withTracing("area") {
        val r2 = radius * radius
        trace("dump r2: $r2")
        PI * r2
    }
```
<div class="fragment" data-fragment-index="2">
```kotlin
val area: Double
    get() = withTracing("area") {
        PI * (radius * radius).dump("dump r2")
    }
```
</div>
<small class="fragment current-only" data-code-focus="3,4" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="9" data-fragment-index="3"></small>

---

### Lokale Extension-Funktionen

```kotlin
class TracingContext(val logger: KLogger, val prefix: String) {
    ...
    fun <T> T.dump(msg: String): T {
        trace("$msg: $this") // entspricht: this@TracingContext.log("$msg: $this")
        return this
    }
}
```
```kotlin
val area: Double
    get() = withTracing("area") {
        PI * (radius * radius).dump("dump r2")
    }
```

<small class="fragment current-only" data-code-focus="3,10"></small>
<small class="fragment current-only" data-code-focus="4">```trace()``` wird beim ```tracingContext``` aufgerufen</small>
<small class="fragment current-only" data-code-focus="4">**Funktion hat zwei  ```this``` Zeiger**</small>

---

### Lokale Extensions in KotlinJS/React

```kotlin
interface TickerState : RState {
    var secondsElapsed: Int
}

class Ticker(props: TickerProps) : RComponent<TickerProps, TickerState>(props) {
    override fun TickerState.init(props: TickerProps) {
        secondsElapsed = props.startFrom
    }

    override fun RBuilder.render() {
        p {
            +"This app has been running for ${state.secondsElapsed} seconds."
        }
    }
}
```
<small class="fragment current-only" data-code-focus="5"></small>
<small class="fragment current-only" data-code-focus="10-14">Lokale Extension-Funktion</small>
<small class="fragment current-only" data-code-focus="10">```RComponent``` definiert abstrakte Extension-Funktion</small>
<small class="fragment current-only" data-code-focus="11">```this``` von ```RBuilder``` benutzen</small>
<small class="fragment current-only" data-code-focus="12">```this``` von ```Ticker``` benutzen</small>

---

### Lokale Extension-Funktionen

* Zusätzliche Funktionen **nur** in einem bestimmten Kontext
* Nutzen:
    * Bereitstellen von Hilfsfunktionen in einem bestimmten Kontext
    * Implizite Übergabe von Kontext (```this```) an Hilfsfunktionen
    * Impliziter Aufruf von Methoden bei zwei Typen <br/>(zwei ```this```-Zeiger)

Note:
  25min

---

### Infix-Funktionen

```kotlin
class Circle(val x: Double, val y: Double, val radius: Double) {
    fun intersects(other: Circle): Boolean ...
}

val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)
println(c1.intersects(c2))
```
<div class="fragment" data-fragment-index="3">
```kotlin
class Circle(val x: Double, val y: Double, val radius: Double) {
    infix fun intersects(other: Circle): Boolean ...
}

val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)
println(c1 intersects c2)
```
</div>
<small class="fragment current-only" data-code-focus="1-3"  data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="7"  data-fragment-index="2"></small>
<small class="fragment current-only" data-code-focus="9, 14"  data-fragment-index="4"></small>


---

### Exposed - Kotlin SQL Library

* DSL für SQL Zugriffe

<br/>

```kotlin
object Addresses : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val city = varchar("city", 50)
}

Addresses.insert { stm ->
    stm[city] = "Hamburg"
}
```

<small class="fragment current-only" data-code-focus="1-4"></small>
<small class="fragment current-only" data-code-focus="2"></small>
<small class="fragment current-only" data-code-focus="6-8"></small>

---

### Infix-Funktionen in Exposed

```kotlin
object Persons : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 50)
    val addressId = integer("address_id") references Addresses.id
}

val personId = Persons.insert {
    it[name] = "Rene"
    it[addressId] = hamburgId
} get Persons.id

Persons.deleteWhere { Persons.name like "%e" }

val allNamesWithCities = (Persons innerJoin Addresses)
            .slice(Persons.name, Addresses.city)
            .selectAll()
```

<small class="fragment current-only" data-code-focus="1-5"></small>
<small class="fragment current-only" data-code-focus="4"></small>
<small class="fragment current-only" data-code-focus="7,10"></small>
<small class="fragment current-only" data-code-focus="12"></small>
<small class="fragment current-only" data-code-focus="14"></small>

---

### Infix-Funktionen

* Funktionen wie Operatoren aufrufen
* Nutzen:
    * Vermeidung von Klammern
    * 'Fluent' APIs
    * Gut geeignet für algebraische Domänen

Note:
  29min

---

### Delegated Properties

```kotlin
class Circle(val radius: Double) {
    val diameter: Double by lazy {
        radius * 2
    }
    val circumference: Double by lazy {
        diameter * PI
    }
}
```
<div class="fragment" data-fragment-index="2">
```kotlin
fun <T> lazy(initializer: () -> T): Lazy<T> = ...

operator fun <T> Lazy<T>.getValue(thisRef: Any?,
                                  property: KProperty<*>): T = value
```
</div>
<small class="fragment current-only" data-code-focus="2,5" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="2,9" data-fragment-index="3"></small>
<small class="fragment current-only" data-code-focus="11,12" data-fragment-index="4"></small>

---

### Gradle - Build-Tool

* Unterstützt Kotlin(Script) als Build-Language

```kotlin
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.2.30"
}
configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}

val clean by tasks

val helloTask by tasks.creating {
    dependsOn(clean)
    doLast { println("Hello") }
}
```
<small class="fragment current-only" data-code-focus="1-3">Lambda++</small>
<small class="fragment current-only" data-code-focus="2">Infix-Funktion</small>
<small class="fragment current-only" data-code-focus="4-6">Lambda++</small>

---

### Delegated Properties in Gradle

```kotlin
val clean = tasks["clean"]

val helloTask = tasks.create("helloTask") {
    dependsOn(clean)
    doLast { println("Hello") }
}
```

```kotlin
val clean by tasks

val helloTask by tasks.creating {
    dependsOn(clean)
    doLast { println("Hello") }
}
```

<small class="fragment current-only" data-code-focus="1,7">```by``` hat Zugriff auf den Property-Namen</small>
<small class="fragment current-only" data-code-focus="3, 9"></small>

---

### Delegated Properties

* Delegieren der lesenden und schreibenden Property-Zugriffe
* Nutzen:
    * Implementierung von verschiedenen Entwurfsmuster <br/>(Proxy, Observer, etc)
    * Vermeidung der doppelter Nennung von Variablennamen

Note:
  33min

---

### Operatoren überladen

```kotlin
fun main(args: Array<String>) {
    val c1 = Circle(x = 100.0, y = 100.0, radius = 50.0)
    val c2 = Circle(x = 75.0, y = 75.0, radius = 5.0)
    println(c1 intersects c2)
    println(c1 % c2)
}

class Circle(val x: Double, val y: Double, val radius: Double) {
    infix fun intersects(other: Circle): Boolean ...

    operator fun mod(other: Circle): Boolean = intersects(other)
}
```
<small class="fragment current-only" data-code-focus="4"></small>
<small class="fragment current-only" data-code-focus="5"></small>
<small class="fragment current-only" data-code-focus="11">Es können nur vorhandene Operatoren überladen werden</small>

---

### Operatoren in KotlinJS/React

```kotlin
div("App-header") {
    key = "header"
    logo()
    h2 {
        +"Welcome to React with Kotlin"
    }
}
p("App-intro") {
    key = "intro"
    +"To get started, edit "
    code { +"app/App.kt" }
    +" and save to reload."
}
```
<small class="fragment current-only" data-code-focus="5,10,12">```unaryPlus()``` Operator</small>

---

### Operatoren in Gradle

```kotlin
tasks {
    "worldTask" {
        dependsOn(helloTask)
        doLast { println("World") }
    }
}
```
<div class="fragment" data-fragment-index="2">
```kotlin
class NamedDomainObjectContainerScope<T : Any>(
    operator fun String.invoke(configuration: T.() -> Unit): T =
        this().apply(configuration)
    ...
```
</div>
<div class="fragment" data-fragment-index="4">
```kotlin
tasks {
    "worldTask"(Zip::class) {
    ...
```
</div>
<small class="fragment current-only" data-code-focus="2" data-fragment-index="1">```invoke()``` Operator am String</small>
<small class="fragment current-only" data-code-focus="8,9" data-fragment-index="3">Lokale Extension-Funktion</small>
<small class="fragment current-only" data-code-focus="12" data-fragment-index="5">???</small>

---

### Operatoren

* Eigene Operatoren für Datentypen definieren
* Nutzen:
    * Kompakterer Code
    * Eher geeignet für mathematische Domänen

Note:
  38min
