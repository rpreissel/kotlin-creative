<br/>
<br/>
<br/>
<br/>
### Weitere Beispiele

---

### KODEIN - Dependency Injection


```kotlin
val kodein = Kodein {
    constant("dburl") with "jdbc:h2:mem:singleton"

    bind<DataSource>() with singleton {
        JdbcDataSource().apply {
            setURL(instance("dburl"))
        }
    }
}

val datasource: DataSource = kodein.direct.instance()

```
<small class="fragment current-only" data-code-focus="1"></small>
<small class="fragment current-only" data-code-focus="2,4"></small>
<small class="fragment current-only" data-code-focus="6,11"></small>

---

### Delegated Properties in Kodein

```kotlin
class DatabaseService(override val kodein: Kodein) : KodeinAware {
    val dataSource: DataSource by instance()
    val dbUrl: String by instance("dburl")
}
```

```kotlin
fun main(args: Array<String>) {
    val kodein = Kodein {
        constant("dburl") with "jdbc:h2:mem:singleton"
        bind<DataSource>() with singleton {
            JdbcDataSource().apply { setURL(instance("dburl")) }
        }
    }
    val databaseService = DatabaseService(kodein)
}
```

<small class="fragment current-only" data-code-focus="2,3"></small>
<small class="fragment current-only" data-code-focus="1"></small>
<small class="fragment current-only" data-code-focus="12"></small>

---

### Anko - Android Extensions

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
<small class="fragment current-only" data-code-focus="1">Lambda++ - ```this``` ist ```AnkoContext```</small>
<small class="fragment current-only" data-code-focus="2">Lambda++ - ```this``` ist ```ConstraintLayout```</small>
<small class="fragment current-only" data-code-focus="3">```hello``` ist ein Attribut der umschliessenden Klasse</small>
<small class="fragment current-only" data-code-focus="5">Lambda++ - ```this``` ist ```ConstraintSetBuilder```</small>
<small class="fragment current-only" data-code-focus="6">Was passiert hier?</small>
<small class="fragment current-only" data-code-focus="6">```invoke()```-Operator am ```TextView```</small>
<small class="fragment current-only" data-code-focus="6">```invoke()```-Operator ist lokale Extension-Funktion im ```ConstraintSetBuilder```</small>
<small class="fragment current-only" data-code-focus="6">Zwei ```this```-Zeiger erlauben die Verknüpfung von ```TextView``` mit ```ConstraintSetBuilder```</small>
<small class="fragment current-only" data-code-focus="7-10">Mehrere Infix-Funktionen</small>

---

### Ktor - Asynchronous Server

```kotlin
@Location("/") class Index()
@Location("/hello/{name}") class Hello(val name: String)

fun Application.main() {
    install(Locations)

    routing {
        get<Index> {
            call.respondHtml {
                body {
                    div {
                        a(locations.href(Hello("Rene"))) {
                            +"Say Hello"
                        }
...
```

<small class="fragment current-only" data-code-focus="4">Extension-Funktion für impliziten Kontext</small>
<small class="fragment current-only" data-code-focus="1,8">Über die Klasse ```Index``` wird der Pfad festgelegt</small>
<small class="fragment current-only" data-code-focus="8,9">```call``` ist ein Property des ```get<Index>```-Lambda++</small>
<small class="fragment current-only" data-code-focus="10,11,12">Builder-Pattern mit Lambda++</small>
<small class="fragment current-only" data-code-focus="13">Operator</small>
<small class="fragment current-only" data-code-focus="2,12">URL wird anhand der ```Hello```-Klasse erzeugt</small>
<small class="fragment current-only" data-code-focus="12">```locations``` kommt über eine Extension-Funktion</small>
<small class="fragment current-only" data-code-focus="5">Was ist das?</small>

---

### Companion-Factory

```kotlin
fun <B, ...> install(feature: ApplicationFeature<...>,
                     configure: B.() -> Unit = {})

open class Locations(val application: Application,
                     val routeService: LocationRouteService) {

    companion object Feature : ApplicationFeature<...> {
        override fun install(pipeline: Application,
                             configure: Locations.() -> Unit): Locations {
            val routeService = LocationAttributeRouteService()
            return Locations(pipeline, routeService).apply(configure)
        }
    }
}

install(Locations)

```
<small class="fragment current-only" data-code-focus="1,2"></small>
<small class="fragment current-only" data-code-focus="4,5"></small>
<small class="fragment current-only" data-code-focus="7-13"></small>
<small class="fragment current-only" data-code-focus="11"></small>
<small class="fragment current-only" data-code-focus="16">Companion wird als Factory übergeben und erzeugt das Feature</small>

---

### Kooby/Jooby - Web Framework

```kotlin
class App: Kooby({
    get("/") {
        val name = param("name").value("Kotlin")
        "Hello $name!"
    }
})

fun main(args: Array<String>) {
    org.jooby.run(::App, *args)
}
```

<small class="fragment current-only" data-code-focus="1-6">Konstruktor mit Lambda++</small>
<small class="fragment current-only" data-code-focus="2-5">```this``` ist ```Request```</small>
<small class="fragment current-only" data-code-focus="9">```::App``` ist Referenz auf Konstruktor</small>
<small class="fragment current-only" data-code-focus="1,9">Vererbung notwendig da API eine Subklasse anfordert</small>

---

### Spek - Specification Framework

```kotlin
object AppTest : Spek({
    jooby(App()) {
        describe("Get /") {
            given("no queryParameter") {
                it("should return Kotlin as the default name") {
                    get("/")
                        .then()
                        .assertThat().statusCode(Status.OK.value())
                        .extract().asString()
                        .let {
                            //Kluent Infix Method
                            it shouldEqual "Hello Kotlin!"
                        }
                }
            }
...
```

<small class="fragment current-only" data-code-focus="1">Konstruktor mit Lambda++ / Subklasse wird von Spek benötigt</small>
<small class="fragment current-only" data-code-focus="3-5">Describe / Given / It</small>
<small class="fragment current-only" data-code-focus="6-13">REST-assured</small>
<small class="fragment current-only" data-code-focus="12">Infix von Kluent</small>
