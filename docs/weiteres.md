# Ideen

## Muster: Interfaces als Extension-Anker
* AnkoLogger zeigen  
    * interface mit einem Property
    * zugehörige Extension-Funktionen
    * Funktionen mit gleichen Namen wie Interface als "Konstruktor"
    * Vorteil: Anwender kann eigentlich nur das Property überschreiben. Wenn die Funktionen als Default-Funktionen im Interface wären, könnte man alles überschreiben

## Muster: getValue für Delegation als Extension-Funktion
* Beispiel Konfig
    * Funktion wird mit getValue()-Extension ausgestattet
    * Vorteil: Sehr gut nachträglich einzubauen
    * Nachteil: Import darf nicht vergessen werden, sonst gibt es Compile-Error
    
## Muster: Companion als Factory, Eigene Configuration, Klasse als Impl
* Ktor zeigen
    * Companion kann Interface implementieren
    * Companion kann Configuration als LammbdaWR bereitstellen
    * Klasse bekommt immutable-Config-Daten und bleibt frei von Initialisierungs-Funktion
    
## Muster: Extension-Funktion um DSL zu erweitern bzw einzusteigen
* Beispiel Ktor: Application.main oder FlowContent.greeter
    * Zugriff auf die "vorhandene" Extension-Funktionen bildet DSL
* Beispiel KotlinJS / React
    * Zugriff auf die "vorhandene" Extension-Funktionen bildet DSL   
                           
## Muster: Eingebettet Extension-Funktionen für Registrierung
* Beispiel: Coroutine-Select + Anko-Layout
    * Zugriff auf zwei This-Zeiger ermöglicht implizites Verknüpfen                      
    
    
---

### Apply: Ext + LambdaWR

```kotlin
fun <T> T.apply(block: T.() -> Unit): T
```

</br>

```kotlin
bind<DataSource>() with singleton {
    JdbcDataSource().apply {
        setURL(instance("dburl"))
    }
}
```
<small class="fragment current-only" data-code-focus="3-5"></small>
<small class="fragment current-only" data-code-focus="4"></small>

---

### Operatoren in Kodein

```kotlin
val kodein = Kodein {
    constant("dburl") with "jdbc:h2:mem:singleton"

    bind<DataSource>() with singleton {
        JdbcDataSource().apply { setURL(instance("dburl")) }
    }
}
```
<div class="fragment" data-fragment-index="2">
```kotlin
interface Kodein {
    ...
    companion object {
        operator fun invoke(allowSilentOverride: Boolean = false,
                            init: Kodein.Builder.() -> Unit
                            ): Kodein = KodeinImpl(allowSilentOverride, init)
    }
}
```
</div>
<small class="fragment current-only" data-code-focus="1" data-fragment-index="1">```invoke()```-Operator am Companion</small>
<small class="fragment current-only" data-code-focus="8" data-fragment-index="3"></small>
<small class="fragment current-only" data-code-focus="11-13" data-fragment-index="4"></small>

---

### Klaxon - JSON Parser

```kotlin
val result: Person? = Klaxon().parse(
    """
    {
      "name": "Rene Preissel",
      "city": "Hamburg"
    }
    """)
```
<br/>
https://github.com/cbeust/klaxon

---

## Lambda++ in Klaxon

```kotlin
val numberArray: JsonArray<Any?> = json { // 'this' ist KlaxonJson
    array(
        listOf(1, 2, 3).map {
            obj(it.toString() to it)
        }
    )
}
```
```
[{
  "1": 1
}, {
  "2": 2
}, {
  "3": 3
}]
```

<small class="fragment current-only" data-code-focus="1-7"></small>
<small class="fragment current-only" data-code-focus="2,8,14">```array()``` ist in ```KlaxonJson```</small>
<small class="fragment current-only" data-code-focus="4,9,11,13 ">```obj()``` ist in ```KlaxonJson```</small>

---

### KODEIN - Dependency Injection


```kotlin
val kodein = Kodein {
    bind<Dice>() with provider { RandomDice(0, 5) }
    bind<DataSource>() with singleton { SqliteDS.open("path/to/file") }
}

```
<br/>

https://github.com/Kodein-Framework/Kodein-DI

---

### Lambda++ in Kodein

```kotlin
val kodein = Kodein { //'this' ist ein Kodein.MainBuilder

    constant("dburl") with "jdbc:h2:mem:singleton"

    bind<DataSource>() with singleton {
        JdbcDataSource().apply {
            setURL(
                instance("dburl")
            )
        }
    }
}
```
<small class="fragment current-only" data-code-focus="1-12"></small>
<small class="fragment current-only" data-code-focus="3"></small>
<small class="fragment current-only" data-code-focus="5-11"></small>
<small class="fragment current-only" data-code-focus="8"></small>

---

### Infix-Funktionen in Kodein

```kotlin
val kodein = Kodein {
    constant("dburl") with "jdbc:h2:mem:singleton"

    bind<DataSource>() with singleton {
        JdbcDataSource().apply {
            setURL(instance("dburl"))
        }
    }

    bind<Random>(tag = "random1") with provider { SecureRandom() }

    bind(tag = "random2") from provider { SecureRandom() }
}
```

<small class="fragment current-only" data-code-focus="2,4"></small>
<small class="fragment current-only" data-code-focus="10,12"></small>
          
          
---

### Generics und das Class-Objekt

```kotlin
class JacksonParser {
    val mapper: ObjectMapper = jacksonObjectMapper()

    fun <T : Any> parse(json: String, kClass: KClass<T>): T {
        return mapper.readValue(json, kClass.java)
    }
}
```
<br/>
<div class="fragment" data-fragment-index="3">
```kotlin
val person: Person? = JacksonParser().parse(jsonString, Person::class)

funWithPerson(JacksonParser().parse(jsonString, Person::class))
```
</div>
<br/>
<small class="fragment current-only" data-code-focus="4" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="5" data-fragment-index="2"></small>
<small class="fragment current-only" data-code-focus="8" data-fragment-index="4">Explizite Übergabe des Klassenobjekts</small>
<small class="fragment current-only" data-code-focus="10" data-fragment-index="5">Explizite Übergabe des Klassenobjekts</small>

---

### Generics mit Reified in Klaxon

```kotlin
class Klaxon {
    ...

    inline fun <reified T> parse(json: String): T?
        = maybeParse(parser(T::class).parse(StringReader(json)) as JsonObject)
}
```
<br/>
<div class="fragment" data-fragment-index="3">
```kotlin
val person: Person? = Klaxon().parse(jsonString)

funWithPerson(Klaxon().parse(jsonString))
```
</div>
<br/>
<small class="fragment current-only" data-code-focus="4" data-fragment-index="1"></small>
<small class="fragment current-only" data-code-focus="5" data-fragment-index="2"></small>
<small class="fragment current-only" data-code-focus="7" data-fragment-index="4">Klasse wird durch Typ der Variablen erkannt</small>
<small class="fragment current-only" data-code-focus="9" data-fragment-index="5">Klasse wird am Parameter der Funktion erkannt</small>

---

### Reified in Kodein

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

<small class="fragment current-only" data-code-focus="6"></small>
<small class="fragment current-only" data-code-focus="11"></small>

---

### Reified

* Implizite Übergabe der generischen Klasse
* Nutzen:
    * Vermeidung von expliziter Übergabe des Class-Objekts
    

              
---

### Invoke vs globale Funktion mit Typnamen

```kotlin
interface Kodein {
    ...
    companion object {
        operator fun invoke(allowSilentOverride: Boolean = false,
                   init: Kodein.Builder.() -> Unit
                   ): Kodein = KodeinImpl(allowSilentOverride, init)
    }
}
```

```kotlin
fun Kodein(allowSilentOverride: Boolean = false,
           init: Kodein.Builder.() -> Unit
           ): Kodein = KodeinImpl(allowSilentOverride, init)
```
<small class="fragment current-only" data-code-focus="1"></small>
<small class="fragment current-only" data-code-focus="3-7"></small>
<small class="fragment current-only" data-code-focus="9-12">Alternative: Globale Funktion mit gleichen Namen wie Typ</small>


              