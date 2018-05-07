# Kotlin Advanced (Arbeitstitel)

## Intro

Ist es Ihnen schwer gefallen die Grundlagen von Kotlin zu lernen?
Vermutlich nicht. Die Syntax ist typischerweise nicht das Problem und die grundlegenden Konzepte sind einfach von Java zu übernehmen. Sehr schnell begeistern  *Data Classes*, *Null Safety*, *Named Parameter* und der kompaktere Code.
Doch wie sieht es mit den Innovationen in Kotlin aus, für die es kein einfaches Equivalent in Java gibt?
*Extension Functions*, *Lambdas with Receiver* und *Delegated Properties* kann man zwar in der Spezifikation nachlesen, doch so schnell erschliesst sich der Einsatz nicht.
Der Artikel beschreibt anhand von realen Projekten, wie diese und weitere Features verwendet werden.

Falls Sie  noch nicht mit den Grundlagen von Kotlin vertraut sind, empfehle ich diesen [Einstieg](https://www.informatik-aktuell.de/entwicklung/programmiersprachen/ist-kotlin-das-bessere-java-eine-einfuehrung.html).


## Extension-Funktionen in kotlinx.html

Das erstes Beispiel zeigt die Benutzung von [*kotlinx.html*](https://github.com/Kotlin/kotlinx.html) - eine Library um HTML zu erzeugen:

    System.out.appendHTML()
        .div {
            h1 {
                text("Features")
            }
            ul {
                li { text("Extension Functions") }
                li { text("Lambdas with receiver") }                
                li { text("Operator overloading") }
            }
        }

Was als erstes auffällt ist, dass es eine neue Funktion ```appendHTML``` bei ```System.out``` gibt.
Im Gegensatz zu Java ist es in Kotlin möglich vorhandene Klassen um Funktionen zu erweitern - **Extension-Functions**. Dadurch spart man sich die üblichen ```Util```-Klassen.

```System.out``` ist ein ```PrintStream``` der wiederum das ```Appendable```-Interface implementiert.  Da die ```appendHTML```-Funktion für alle ```Appendable```-Klassen nützlich ist, wurde für das Interface eine Extension-Funktion definiert:
    
    fun Appendable.appendHTML(prettyPrint : Boolean = true) : TagConsumer = ...

Beachten Sie den Klassennamen vor dem eigentlichen Funktionsnamen.
Extension-Funktionen müssen explizit importiert werden und können niemals ein vorhandene Member-Funktion überschreiben.

    import kotlinx.html.stream.appendHTML

## Lambdas with Receiver    

Das Ergebnis von ```appendHTML()``` ist ein Objekt der Klasse ```TagConsumer```. Diese Klasse dient als Einstiegspunkt in die DSL und stellt für jedes HTML-Tags eine entsprechende Builder-Funktion mit einen Konfigurations-Lambda bereit:
    
    System.out.appendHTML()
        .div() {
            ...
        }    

In dem Beispiel können Sie schön sehen, dass man den Lambda-Block hinter den Funktionsaufruf setzen kann, d.h. ausserhalb der Parameterklammern. Leere Parameterklammern können auch ganz weggelassen werden.

Etwas vereinfacht sieht die ```div```-Funktion so aus:

    fun TagConsumer.div(block : DIV.() -> Unit) : DIV {
        val div = DIV(this)
        div.block()
        return div
    }

Die Funktion ```div``` fordert als Paramteter ein **Lambda with receiver**: ```DIV.() -> Unit```. Im Gegensatz zu normalen Lambdas: ```() -> Unit```, wird vor der Parameterliste noch ein Typ definiert. Dieses Lambda kann  nur an einem Objekt dieses Typs ausgeführt werden: ```div.block()``` und der ```this``` Zeiger innerhalb des Lambda-Blocks wird auf das benutzte Objekt gesetzt. In folgendem Beispiel wird also im ersten Block der ```this```-Zeiger auf ein ```DIV```-Objekt und im zweiten Block auf ein ```UL```-Objekt gesetzt:

    appendHTML()
        .div { // this ist DIV
             ul { // this ist UL                 
                li { // this ist LI und die Funktion li() ist in UL definiert
                    text("Lambdas with receiver")
                }                                
            }
        }


Nur der erste ```div()```-Aufruf wird direkt bei der Klasse ```TagConsumer``` durchgeführt wird. Alle weiteren
Verschachtelungen werden beim jeweiligen ```this```-Zeiger ausgeführt. Dadurch ist es möglich syntaktisch korrekte Reihenfolgen zu erzwingen, z.B. das ein ```<li>``` nur innerhalb eines ```<ul>``` bzw. ```<ol>``` aufgerufen werden kann (für mehr Details siehe [hier](https://kotlinlang.org/docs/reference/type-safe-builders.html). Ausserdem wird durch den Aufruf beim jeweiligen Tag-Objekt eine baumartige Struktur erzeugt - wir sehen also hier das Builder-Pattern in Kotlin.
  
## Extension-Funktionen für die Erweiterng von DSLs

Nehmen wir mal an, dass Menueinträge folgendermaßen definiert werden:

    div {
        a("./link1") {
            h2 { text("Menu 1") }
        }
        a("./link22") {
            h2 { text("Menu 2") }
        }
    }

Es ist sofort offensichtlich, dass es hier Code-Duplikationen gibt -  die Struktur ist immer gleich nur der Titel und die URL ändert sich.
Nützlich wäre es einen einzelnen Menüeintrag als eigene Komponente - eigene Funktion - bereitzustellen.
Das geht bei *kotlinx.html* sehr einfach durch *Extension-Funktionen* an der geeigneten Tag-Klasse.
Im konkreten Fall sollen Menueinträge überall dort erlaubt sein, wo auch das ```<a>```-Tag möglich ist. Ein kurze Suche nach der ```a()```-Funktion bringt die Basisklasse ```FlowOrInteractiveOrPhrasingContent``` zum Vorschein. 
Eine Extension-Funktion ```menuEntry()``` ist schnell definiert und kann ihrerseits die vorhandenen Tag-Funktionen nutzen um den Menueintrag zu bauen. Wenn notwendig könnte man auch noch einen eigenen Lambda-Block definieren um weitere Verschachtelung zu ermöglichen.

    fun FlowOrInteractiveOrPhrasingContent.menuEntry(title: String, href: String) =
        a(href) {
            h2 { text(title) }
        }

Der Aufruf der Funktion sieht genauso aus, wie die bei den vorhandenen Tag-Funktionen:

    div {
        //Aufruf mit Named-Parameter macht den Code lesbarer
        menuEntry(title = "Menu 1", href = "./link1") 
        menuEntry(title = "Menu 2", href = "./link2")
    }


Das gezeigte Muster aus Builder-Funktionen mit Lambdas und die Erweiterbarkeit der DSL mittels Extension-Funktionen sieht man in vielen Kotlin-Libraries. Im Gegensatz zu Java ist die Integration von eigenen Erweiterungen sehr einfach ohne Vererbung oder Hilfsklassen möglich. Durch Extension-Funktionen ist es auch dem Library-Entwickler möglich, die eigenen DSL-Funktionen auf verschiedene Module zu verteilen. Das sehen wir uns gleich beim asynchronen Webframework [*Ktor*](https://ktor.io/) genauer an.

## Ktor - Asynchrones Webframework

Einen ersten Eindruck von *Ktor* erhält man durch das folgende Beispiel:

    @Location("/hello/{name}") 
    class Hello(val name: String)

    fun Application.greeter() {
        install(Locations)

        install(Routing) {
            get<Hello> { hello ->
                call.respondHtml {
                    body {
                        h1 { text("Hello ${hello.name}") }
                    }
                }
            }
        }
    }            

Im Beispiel wird beim Aufruf der URL ```http://<server:port>/hello/Rene``` mit ```<h1>Hello Rene</h1>``` geantwortet. Das Erzeugen des HTML-Markups erfolgt mit Hilfe von *kotlinx.html*.

Im Gegensatz zu vielen Java-basierenden Webframeworks muss man in *Ktor* nicht von einer Basisklasse erben, sondern nur eine Extension-Funktion bereitstellen: ```Application.greeter()```. Der Name ist dabei unerheblich. Innerhalb der Extension-Funktion kann man direkt auf alle weiteren Funktionen der Konfigurations-DSL zugreifen. Im Beispiel ist die Funktion ```install()``` zu sehen, die übergebene Features installiert und konfiguriert. 

Beim Starten des Servers muss die Extension-Funktion als Referenz übergeben werden. Intern wird dann ein geeignetes ```Application```-Objekt erzeugt und die Funktion darauf angewendet.


    fun main(args: Array<String>) {
        val server = embeddedServer(
            Netty, port = 8080, module = Application::greeter
        )
        server.start(wait = true)
    }

Während Extension-Funktionen bei *kotlinx.html*  genutzt werden um die DSL zu erweitern, nutzt *Ktor* dieses Feature um die Anwendung der vorhandenen DSL zu vereinfachen - man hat direkten Zugriff auf alle DSL-Funktionen.
Die Java-Alternative wäre, das ```Application```-Objekt als Parameter der ```greeter```-Funktion zu übergeben und die Konfigurationsfunktionen explizit aufzurufen.

 ## Mit Reified Class-Parameter sparen  

 Eine weitere interessante Anwendung von Kotlin-Features ist beim Konfigurieren des Routings zu sehen. Das folgende Beispiel zeigt wie ein Handler für einen ```GET```-Aufruf auf der URL ```/hello/{name}``` registriert wird:

    @Location("/hello/{name}") 
    class Hello(val name: String)
    
    get<Hello> { hello ->
        ...
    }
    
Innerhalb der ```get```-Funktion muss *Ktor* per Reflektion auf das ```Hello```-Klassenobjekt zugreifen, um die URL aus der ```@Location```-Annotation auszulesen.
Da Kotlin auf der JVM die gleichen Einschränkungen hat wie Java, stehen die generischen Typparameter zur Laufzeit nicht zur Verfügung (type erasure). Also müsste man in Java das Class-Objekt explizit als Parameter übergeben.
In Kotlin gibt es dafür '**inline reified**'. Im folgenden Beispiel ist ein Auszug der ```get```-Funktion zu sehen:

    inline fun <reified T : Any> Route.get( ... ): Route {
        return location(T::class) {
            ...
        }
    }

Das Schlüsselwort ```inline``` sorgt dafür, dass die ganze Funktion an die Aufrufstelle kopiert wird.
Durch das Schlüsselwort ```reified``` am Typparameter, übergibt Kotlin die ermittelte generische Klasse implizit an den Funktionsrumpf.  Dadurch ist der Zugriff auf das Class-Objekt: ```T::class``` möglich. 
Mit ```reified``` gelingt es sehr oft die explizite Übergabe der Klasse als Parameter zu umgehen und dadurch unnütze Duplikationen zu vermeiden und den Code lesbarer zu gestalten.

## Interne Modularität durch Extensions

Ist Ihnen im vorigen Abschnitt aufgefallen, dass die ```get```-Funktion als Extension-Funktion an der Klasse ```Route``` implementiert wurde?

    package io.ktor.locations

    inline fun <reified T : Any> Route.get( ... ): Route 

Das Konzept *Routing* - das Mapping von URLs auf Handler - ist bei *Ktor* bereits im Kern verankert. Allerdings ist die Definition des Routings mittels ```@Location```-Annotations eine Erweiterung, die in einem seperaten Modul implementiert wird: ```io.kto.locations```

Diese interne Modularität wird durch Extension-Funktionen sehr elegant, ohne Vererbung, ohne explizite Delegation und ohne vorbereitende Massnahmen (*Plugins* etc) an der ```Route```-Klasse ermöglicht.

## Companion als Factory

Wie gerade beschrieben, gibt es in *Ktor* verschiedene Features. Diese müssen vor der Benutzung installiert und konfiguriert werden, z.B. für das Logging von einzelnen Requests:

    fun Application.main() {
        install(CallLogging) { // this ist CallLogging.Configuration
            level = Level.INFO
        }
        ...
    }

Bei der   ```install```-Funktion wird eine interessante Mischung von Kotlin-Features verwendet. Die Signatur sieht vereinfacht wie folgt aus:

    fun <B, F> Application.install(feature: ApplicationFeature<B, F>, configure: B.() -> Unit = {}): F

Die Extension-Funktion an der Klasse ```Application``` bekommt ein ```ApplicationFeature``` und einen Konfigurationsblock übergeben.
Der generische Typparameter ```B``` steht für eine Builder-Klasse die bei der Konfiguration des Features mit einem Lambda-Block benutzt wird. Im Beispiel ist das ```CallLogging.Configuration```.

Der generische Typparameter ```F``` steht für das eigentliche Feature. Im Beispiel ist das ```CallLogging```.
 
Beim Aufruf der ```install```-Funktion wird die ```CallLogging```-Klasse übergeben, d.h. irgendwie muss die Klasse sich wie ein Objekt verhalten, welches das ```ApplicationFeature```-Interface implementiert. Genau dafür gibt es in Kotlin das **Companion**-Konzept. Zu einer Klasse kann ein zugehöriges Objekt definiert werden, welches immer genutzt wird, wenn die Klasse die Rolle eines Objektes einnimmt.
In der Klasse ```CallLogging``` gibt es also ein *Companion*-Objekt, welches das ```ApplicationFeature```-Interface implementiert:

    class CallLogging(...) {
        class Configuration { ... }

        companion object Feature : ApplicationFeature<Configuration, CallLogging> {
            override fun install(pipeline: Application, configure: Configuration.() -> Unit): CallLogging {
                ...
                val configuration = Configuration()
                configuration.configure()
                val feature = CallLogging(..., configuration.level, configuration.filters.toList())
                ...
                return feature
            }
        }
    }

Zusätzlich gibt es noch die innere Klasse ```Configuration```, welche die Rolle des Builders ```B``` einnimmt. In der  ```install```-Funktion wird ein Objekt der ```Configuration```-Klasse angelegt und und darauf das Lambda aufgerufen. Innerhalb des Lambdas kann das ```Configuration```-Objekt verändert werden: ```level = Level.INFO```. Als Ergebnis der  ```install```-Funktion wird ein neues Objekt der ```CallLogging```-Klasse mit den Konfigurationsdaten angelegt. 

Das Zusammenspiel von *Companion*, Konfigurationsklasse und *Lambda with receiver* ermöglicht es, die Schnittstelle für die Konfiguration von der API des eigentlichen Features zu separieren. 

## Companion als statischer Ersatz

*Companions* können auch genutzt werden um statische Funktionen in Kotlin zu simulieren. Das schauen wir uns am Beispiel des Logging-Frameworks [kotlin-logging](https://github.com/MicroUtils/kotlin-logging) an: 

    class Circle(val radius: Double) {
        val diameter: Double
            get() {
                logger.info("compute diameter")
                return radius * 2
            }

        companion object : KLogging()
    }

Im Beispiel wird in der Getter-Funktion für das ```diameter```-Property auf einen Logger: ```logger``` zugegriffen. Typischwerweise würde man in Java den Logger als ```static``` definieren, damit es nur eine Instanz pro Klasse gibt. In Kotlin gibt es kein ```static```. Allerdings kann man durch ein *Companion*-Objekt das gleiche Verhalten erreichen.
Im Beispiel wird ein  *Companion*-Objekt angelegt, welches von der Klasse ```KLogging``` erbt. In dieser Klasse wird das Property ```logger``` definiert:

    class KLogging : KLoggable {
        override val logger: KLogger = logger()
    }

Innerhalb der Klasse ```Circle``` wird beim Benutzen von Properties und Methoden nicht nur in der eigenen Klasse nach den entsprechenden Eigenschaften gesucht, sondern auch im zugehörigen *Companion*-Objekt. Deswegen kann im Beispiel das  ```logger```-Property benutzt werden. Es ist auch möglich ausserhalb der Klasse ```Circle``` das ```logger```-Property anzusprechen. Dann steht die Klasse ```Circle``` wieder anstelle eines Objektes und das zugehörige *Companion*-Objekt wird benutzt:

    Circle.logger.info("static access")

## Kodein - Dependency Injection mit Kotlin

Den meisten wird bei Dependency Injection [Spring](http://spring.io/) einfallen. Natürlich kann man  Spring mit Kotlin nutzen. Seit der neuesten Version *'Spring 5'* gibt es sogar schon eingebaute [Extension-Funktionen](https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/kotlin.html) um die Möglichkeiten von Kotlin noch besser ausznutzen.

Nachfolgend werde ich allerdings ein anderes Dependency Injection Framework: [Kodein](https://github.com/Kodein-Framework/Kodein-DI) vorstellen, weil es dort noch zwei interessante Sprach-Features zu entdecken gibt:

    // Dependency-Kandidaten konfigurieren
    val kodein = Kodein { // this ist Kodein.MainBuilder
        constant("dburl") with "jdbc:h2:mem:singleton"

        bind<DataSource>() with singleton {
            JdbcDataSource().apply {
                setURL(instance("dburl"))
            }
        }
    }

    // Dependencies nutzen
    val datasource: DataSource = kodein.direct.instance()

Wie in allen DI-Frameworks gibt es eine Möglichkeit Objekte als Dependency-Kandidaten zu definieren. Dazu nutzt *Kodein* schon das bekannte Builder-Pattern mit einen *Lambda with receiver*-Block. Im Beispiel sieht man die beiden Funktionsaufrufe ```constant("dburl")``` und ```bind<DataSource>()```. Diese werden an der Klasse ```Kodein.MainBuilder``` aufgerufen, welche als ```this```-Zeiger in dem Konfigurationsblock dient.

## Infix-Funktionen in Kodein

Vielleicht ist Ihnen schon aufgefallen das nach den Funktionsaufrufen das Wort ```with``` auftaucht. Das ist kein Schlüsselwort der Sprache *Kotlin*, sondern eine Funktion die als ```infix``` definiert wurde. Vereinfacht sieht das folgendermaßen aus:

    class TypeBinder {
        infix fun with(binding: KodeinBinding<...>): Unit = ...
    }
    
*Infix*-Funktionen werden ähnlich wie Operatoren (z.B. ```+```) zwischen die Argumente ohne Punkt und ohne Klammern geschrieben.
Im nächsten Code-Beispiel sehen Sie, wie die ```with```-Funktion als normaler Aufruf aussehen würde:

    bind<DataSource>().with(singleton {
        JdbcDataSource().apply {
            setURL(instance("dburl"))
        }
    }) 

An diesem Beispiel kann man gut sehen, dass *Infix*-Funktionen weniger Klammern brauchen und damit zur besseren Lesbarkeit des Codes beitragen können.

Inhaltlich sorgt die ```with```-Funktion dafür, das die Konstante ```dburl``` an eine JDBC-URL gebunden wird.
Beim ```bind<DataSource>```-Aufruf wird für das Interface ```DataSource``` ein Singleton als Kandidat gebunden:  ```JdbcDataSource```. 

Die ```bind```-Funktion ist übrigens auch wieder als ```inline reified``` definiert, dadurch kann der generische Typparameter: ```DataSource``` innerhalb der Funktion benutzt werden und muss nicht noch zusätzlich als Class-Objekt übergeben werden. 

    // Dependencies nutzen
    val datasource: DataSource = kodein.direct.instance()

Das Gleiche gilt für die ```instance()```-Funktion, die beim Ermitteln der Abhängigkeit benutzt werden kann. Auch diese ist  als ```inline reified``` definiert und nutzt den Typ der geforderten Variable als impliziten Parameter.

## Delegated Properties

Bisher haben wir noch kaum das eigentliche Dependency **Injection** gesehen. Typischerweise werden dabei Abhängigkeiten in Services injiziert. Schauen wir uns dazu ein Beispiel in *Kodein* an:

    class DatabaseService(override val kodein: Kodein) : KodeinAware {
        val dbUrl: String by instance("dburl")
        val dataSource: DataSource by instance()   

        ...     
    }

Es gibt also einen ```DatabaseService``` der zum Arbeiten sowohl die ```dbUrl```, als auch die ```dataSource``` benötigt.
Im Beispiel werden die beiden Properties mit ```by instance(...)``` initialisiert.
In diesem Fall handelt sich bei ```by``` tatsächlich um ein Schlüsselwort in *Kotlin*, welches bei der Initialisierung von Properties verwendet werden kann. Normalerweise generiert *Kotlin* für alle Properties einfache ```get```- und ggf. ```set```-Methoden. Mit dem Schlüsselwort ```by``` kann man ein Objekt definieren, welches als *Delegate* benutzt wird. Das heisst, immer wenn ein ```get```-Zugriff auf ein solches Property erfolgt, wird der Aufruf an das Delegation-Objekt weitergeleitet. 

Sehen wir uns den Mechanismus in *Kodein* an.
Als ersten Schritt muss der ```DatabaseService``` das ```KodeinAware```-Interface implementieren. Dieses Interface erzwingt das Bereitstellen eines ```kodein```-Properties.

    interface KodeinAware {
        val kodein: Kodein
        ...
    }

Im Falle des ```DatabaseService``` wird das Property gleich beim Konstruktor übergeben und definiert:

    class DatabaseService(override val kodein: Kodein) : KodeinAware {
        ...

*Kodein* definiert für das ```KodeinAware```-Interface eine *Extension Funktion*: ```instance```. Diese dient zum Erzeugen eines speziellen Delegation-Objekts vom Typ ```KodeinProperty```:

    inline fun <reified T : Any> KodeinAware.instance(tag: Any? = null): KodeinProperty<T> = ...

Innerhalb der  ```instance```-Funktion kann auf das ```kodein```-Property  von ```KodeinAware``` zugegriffen werden.
Zusätzlich ist durch *inline reified* der Zugriff auf den generischen Typparameter - der Typ des zu initialisierenden Properties - möglich.
Beide Informationen werden an das neu erzeugte ```KodeinProperty``` übergeben. Erfolgt nun der Zugriff auf ein solches *Delegated Property* kann das Delegation-Objekt in der ```kodein```-Instanz nach dem geeigneten Dependency Kandidaten suchen. 

Wenn Sie eine eigenes Delegation-Objekt implementieren wollen, dann finden Sie [hier](https://kotlinlang.org/docs/reference/delegated-properties.html) die Details.

Der ```DatabaseService``` kann anschliessend einfach erzeugt werden und benötigt nur eine ```kodein```-Instanz:

    val kodein = Kodein { 
        constant("dburl") with "jdbc:h2:mem:singleton"
        bind<DataSource>() with singleton { JdbcDataSource().apply { setURL(instance("dburl")) } }
    }

    val databaseService = DatabaseService(kodein)

## Alles zusammen und schütteln

Im vorigen Abschnitt bekam der  ```DatabaseService``` eine ```kodein```-Instanz im Konstruktor übergeben. Das koppelt diesen Service stark an das verwendete Dependency-Injection-Framework. Auch das Testen wird schwieriger, da immer eine *Kodein*-Instanz erzeugt werden muss. Im Folgenden soll gezeigt werden, wie es anders gehen kann und gleichzeitig sehen wir ein interessantes Zusammenspiel von *Delegated Properties*, *Lambda with Receiver* und *Reified*.

    class DatabaseService(val dataSource: DataSource, val dbUrl: String) {
        ...
    }

In diesem Fall bekommt der ```DatabaseService``` die benötigten Daten direkt im Konstruktor übergeben. Das Erzeugen des Services könnte so aussehen:

    val databaseService by kodein.newInstance { //this ist DKodein
        DatabaseService(instance(), instance("dburl"))
    }

Die ```kodein```-Instanz ist wieder mit der ```dburl``` und der ```DataSource``` konfiguriert wurden. Zum Erzeugen nutzen wir  auch wieder ein *Delegated Property* mit dem ```by```-Schlüsselwort. Dazu wird die Funktion ```newInstance``` am ```kodein```-Objekt aufgerufen.
Als Parameter wird ein *Lambda with Receiver* übergeben. In diesem Lambda ist der ```this```-Zeiger auf eine ```DKodein```-Instanz gesetzt. Diese stelt die ```instance```-Funktion für den Zugriff auf die konfigurierten Objekte bereit.
Da die ```instance```-Funktion wieder *inline reified* ist kann auf alle expliziten Typparameter verzichtet werden.
Das Ergebnis des Lambda-Block wird der Variable ```databaseService``` zugewiesen.

Ich finde es beeindruckend, dass der Datentyp ```DatabaseService``` nur ein einziges Mal explizit hingeschrieben werden muss und die Datentypen für die ```dbUrl``` und die ```DataSource``` gar nicht explizit auftauchen. 

## Alles geht, nichts muss!

Ich habe in dem Artikel bewusst einige komplexere Sprach-Features von Kotlin vorgestellt. Es soll kein Ansporn sein alle diese Features in Ihrer Anwendung zu benutzen. Richtig angewendet - bei eigenen Basis-Bibliotheken und eigenen DSLs - erlauben diese Features interessante Konstruktionen und können zu "schönen" kompakten fachlichen Code führen.
Falsch oder übermässig angewendet können diese Features aber auch zu unverständlichen und komplexen Code führen.

Wer noch tiefer in das Thema einsteigen will, sollte sich die Möglichkeit [Operatoren zu überladen](https://kotlinlang.org/docs/reference/keyword-reference.html) und [lokale Extension-Funktionen](https://kotlinlang.org/docs/reference/extensions.html#declaring-extensions-as-members) anschauen. 
Beides kann man ganz gut im Projekt [React Kotlin](https://github.com/JetBrains/create-react-kotlin-app) in Aktion sehen.