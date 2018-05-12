# Innovative Sprach-Features in Kotlin

## Intro

Ist es Ihnen schwergefallen, die Grundlagen von Kotlin zu lernen?
Vermutlich nicht. Die Syntax ist typischerweise nicht das Problem und die grundlegenden Konzepte sind einfach von Java zu übernehmen.
Sehr schnell begeistern *Data Classes*, *Null Safety*, *Named Parameter* und der kompaktere Code.
Doch wie sieht es mit den Innovationen in Kotlin aus, für die es kein einfaches Äquivalent in Java gibt?
*Extension Functions*, *Lambdas with Receiver* und *Delegated Properties* kann man zwar in der Spezifikation nachlesen,
doch so schnell erschließt sich der Einsatz nicht. Der Artikel beschreibt anhand von realen Projekten, wie diese und
weitere Features verwendet werden.

Falls Sie noch nicht mit den Grundlagen von Kotlin vertraut sind, empfehle ich diesen
[Einstieg](https://www.informatik-aktuell.de/entwicklung/programmiersprachen/ist-kotlin-das-bessere-java-eine-einfuehrung.html).

Die folgenden Beispiele finden Sie in diesem [Github-Projekt](https://github.com/rpreissel/kotlin-creative/tree/master/artikel).

## Extension-Funktionen - Weg mit den Util-Klassen

**Extension-Funktionen** ermöglichen es, vorhandene Klassen um neue Funktionalitäten zu erweitern.
In Java würde man für die Erweiterung von Klassen entweder Vererbung oder die typischen ```Util```-Klassen benutzen.
Schauen wir uns als Beispiel [*kotlinx.html*](https://github.com/Kotlin/kotlinx.html) - eine Bibliothek um HTML-Markup zu erzeugen - an:

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
Diese Funktion erlaubt es HTML-Markup auf die Konsole auszugeben.
In Java würde man vermutlich eine ```HTMLUtil```-Klasse mit einer statischen Hilfsfunktion anlegen:

```
    //Java-Code
    public class HTMLUtil {
        public static TagConsumer append(Appendable out) {
            ...
        }
    }

    HTMLBuilder.append(System.out)
        .div( ... )
```

In Kotlin sieht die Definition von ```appendHTML``` als *Extension-Funktion* wie folgt aus:
    
    fun Appendable.appendHTML() : TagConsumer = ...

Beachten Sie den Interface-Namen vor dem eigentlichen Funktionsnamen. Dadurch wird diese Funktion als *Extension*
für das ```Appendable```-Interface definiert. Bei jeder Klasse, welches dieses Interface implementiert,
kann diese Funktion nun genau wie eine normale Member-Funktion aufgerufen werden, so auch bei ```PrintStream```.

Extension-Funktionen müssen explizit importiert werden und können vorhandene Member-Funktion nicht überschreiben.

    import kotlinx.html.stream.appendHTML

## Builder mit Lambdas with Receiver    

Zurück zu dem ersten Code-Beispiel. Dort ist nach dem ```appendHTML```-Aufruf zu erahnen, wie das HTML-Markup als eine
Reihe von verschachtelten Funktionsaufrufen erstellt wird. Wir schauen uns als nächstes die Konzepte und Syntax für
die Erstellung von solchen hierarchischen Strukturen in Kotlin an.

Die Funktion ```appendHTML()``` liefert ein Objekt der Klasse ```TagConsumer``` zurück. Diese Klasse dient als Einstiegspunkt,
um das HTML-Markup zu erzeugen. Für jedes HTML-Tag steht eine entsprechende Builder-Funktion bereit, z.B: ```div```:
    
    System.out.appendHTML() // appendHTML() liefert einen TagConsumer
        .div() {
            ...
        }    


In dem Beispiel können Sie schön sehen, dass nach den Parameterklammern der ```div```-Funktion ein Lambda-Block ```{ ... }```
übergeben wird. Anders als in Java können Lambda-Blöcke außerhalb der Parameterklammern angegeben werden.
Leere Parameterklammern können auch ganz weggelassen werden, so dass nach dem Funktionsnamen direkt die geschweiften Klammern stehen.
Dadurch sieht der Funktionsaufruf fast wie eine eigene Kontrollstruktur aus: ```div { }```.

Die ```div```-Funktion ist in *kotlinx.html* etwas vereinfacht folgendermaßen definiert:

    fun TagConsumer.div(block : DIV.() -> Unit) : DIV {
        val div = DIV(this)
        div.block()
        return div
    }

Die ```div```-Funktion bekommt, wie bereits beim Aufruf gesehen, ein Lambda übergeben: ```DIV.() -> Unit```.
Dabei handelt es sich um ein spezielles Lambda - ein **Lambda with Receiver**. Im Gegensatz zu normalen Lambdas: ```() -> Unit```,
wird vor der Parameterliste noch ein Typ definiert. Im Beispiel ist es die ```DIV```-Klasse.

Dieses Lambda kann nur mit einem Objekt dieses Typs ausgeführt werden. Deswegen legt die ```div```-Funktion als Erstes
ein Objekt vom Typ ```DIV``` an und ruft anschließend das Lambda auf: ```div.block()```.
Dabei wird innerhalb des Lambdas der ```this```-Zeiger auf das benutzte Objekt gesetzt.
Im folgenden Beispiel wird also im ersten Block der ```this```-Zeiger auf ein ```DIV```-Objekt, im zweiten Block auf
ein ```UL```-Objekt und im dritten Block auf ein ```LI```-Objekt gesetzt:

    appendHTML()
        .div { // this ist DIV
             ul { // this ist UL                 
                li { // this ist LI und die Funktion li() ist in UL definiert
                    text("Lambdas with receiver")
                }                                
            }
        }


Nur der ```div```-Aufruf direkt hinter ```appendHTML``` wird bei der Klasse ```TagConsumer``` durchgeführt. Alle weiteren
Verschachtelungen werden beim jeweiligen ```this```-Zeiger des Lambdas ausgeführt, also ```DIV.ul()```, ```UL.li()``` und ```LI.text()```.
Dadurch ist es möglich, syntaktisch korrekte Reihenfolgen zu erzwingen, z.B. dadurch, dass die ```li```-Funktion nur
innerhalb der ```UL```- bzw. ```OL```-Klasse existiert.
Mehr Details zum [Builder-Pattern](https://de.wikipedia.org/wiki/Erbauer_(Entwurfsmuster)) in Kotlin finden Sie
[hier](https://kotlinlang.org/docs/reference/type-safe-builders.html).
  
## Extension-Funktionen für die Erweiterung von DSLs

Mit *kotlinx.html* kann man also sehr kompakt HTML-Markup erzeugen. Im Zusammenspiel mit den bereits bekannten *Extension-Funktionen*
ist es auch einfach möglich, eigene Erweiterungen in die HTML-DSL
([Domain Specific Language](https://de.wikipedia.org/wiki/Dom%C3%A4nenspezifische_Sprache)) einzubauen.

Schauen wir uns das am Beispiel von Menüeinträgen an:

    div {
        a("./link1") {
            h2 { text("Menu 1") }
        }
        a("./link2") {
            h2 { text("Menu 2") }
        }
    }

Es ist sofort offensichtlich, dass es hier Code-Duplikationen gibt -  die Struktur ist immer gleich, nur der Titel und die URL ändert sich.
Nützlich wäre es, einen einzelnen Menüeintrag als eigene Komponente - eigene Funktion - bereitzustellen.
Das geht bei *kotlinx.html* sehr einfach durch eine *Extension-Funktion* an der geeigneten Tag-Klasse.
Im konkreten Fall sollen Menüeinträge überall dort erlaubt sein, wo auch das ```<a>```-Tag möglich ist. Ein kurze Suche
nach der ```a()```-Funktion bringt die Basisklasse ```FlowOrInteractiveOrPhrasingContent``` zum Vorschein.
Eine Extension-Funktion ```menuEntry``` ist schnell definiert und kann ihrerseits die vorhandenen Tag-Funktionen nutzen,
um den Menüeintrag zu bauen:

    fun FlowOrInteractiveOrPhrasingContent.menuEntry(title: String, href: String) =
        a(href) {
            h2 { text(title) }
        }

Der Aufruf der Funktion sieht ähnlich aus, wie bei der standardmäßig vorhandenen ```text```-Funktion:

    div {
        //Aufruf mit Named-Parameter macht den Code lesbarer
        menuEntry(title = "Menu 1", href = "./link1") 
        menuEntry(title = "Menu 2", href = "./link2")
    }


Das gezeigte Muster aus Builder-Funktionen mit Lambdas und die Erweiterbarkeit der DSL mittels Extension-Funktionen
sieht man in vielen Kotlin-Libraries. Im Gegensatz zu Java ist die Integration von eigenen Erweiterungen sehr einfach
ohne Vererbung oder Hilfsklassen möglich. Das sehen wir uns im nächsten Abschnitt beim asynchronen Webframework
[*Ktor*](https://ktor.io/) genauer an.

## Ktor - Asynchrones Webframework

*Ktor* ist selber kein Webserver, sondern basiert auf vorhandenen Webservern, z.B. [Netty](https://netty.io/). *Ktor*
erlaubt es, Request-Handler für URLs zu definieren. Beim Aufruf der URL wird dann der Handler ausgeführt und dieser kann
mit HTML, JSON oder jeden beliebigen anderen Format antworten.

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

Im Beispiel wird beim Aufruf der URL ```http://<server:port>/hello/Rene``` mit ```<body><h1>Hello Rene</h1></body>``` geantwortet.
Das Erzeugen des HTML-Markups erfolgt mit Hilfe von *kotlinx.html*.

## Extension-Funktionen als Ersatz für Vererbung

Im Gegensatz zu vielen Java-basierten Webframeworks muss man in *Ktor* nicht von einer Basisklasse erben, sondern nur
eine Extension-Funktion bereitstellen: ```Application.greeter()```. Der Name ist dabei unerheblich.
Dadurch, dass man die Klasse ```Application``` erweitert, kann man innerhalb der *Extension-Funktion* direkt auf alle
weiteren Funktionen der Konfigurations-DSL zugreifen. Im Code ist zum Beispiel die Funktion ```install``` zu sehen,
die ein übergebenes Features installiert und konfiguriert.

Beim Starten des Servers muss die *Extension-Funktion* als Referenz: ```Application::greeter``` übergeben werden.
Intern wird dann ein geeignetes ```Application```-Objekt erzeugt und die Funktion darauf angewendet.


    fun main(args: Array<String>) {
        val server = embeddedServer(
            Netty, port = 8080, module = Application::greeter
        )
        server.start(wait = true)
    }

Während *Extension-Funktionen* bei *kotlinx.html* genutzt werden, um eine DSL zu erweitern, nutzt *Ktor* dieses Feature,
um den Aufruf der vorhandenen DSL zu vereinfachen. Die Java-Alternative wäre, das ```Application```-Objekt als Parameter
an die ```greeter```-Funktion zu übergeben und die Konfigurationsfunktionen explizit aufzurufen oder von der
```Application```-Klasse zu erben.

## Lambda with Receiver in Ktor

Auch bei *Ktor* werden *Lambdas with Receiver* oft eingesetzt. Das folgende Beispiel zeigt, wie ein Handler für die
URL ```/hello/{name}``` registriert wird. Darin sind drei Lambda-Blöcke zu sehen, die jeweils den ```this```-Zeiger redefinieren:
    
    @Location("/hello/{name}") 
    class Hello(val name: String)

    get<Hello> { hello ->              // this ist PipelineContext
        call.respondHtml {             // this ist HTML
            body {                     // this ist BODY 
                ...
            }
        }
    }

Interessant ist das erste Lambda, welches direkt an die ```get<Hello>```-Funktion übergeben wird. Die Funktion hat einerseits
einen expliziten Parameter: ```hello```, welcher die Daten des URL-Aufrufs kapselt. Zusätzlich wird der ```this```-Zeiger
auf einen ```PipelineContext``` gesetzt.  Der ```this```-Zeiger wird redefiniert, damit man auf das ```call```-Property
des ```PipelineContext``` zugreifen kann. Dieses Property hält die zusätzlichen Request-Parameter und wird genutzt,
um den Response zu erzeugen: ```respondHtml```. Würde man nur ein normales Lambda nutzen, müsste der ```PipelineContext```
explizit als weiterer Parameter übergeben werden.

## Mit Reified Class-Parameter sparen  

Das vorige Beispiel zeigt noch ein weiteres interessantes Kotlin-Feature. Haben Sie sich mal überlegt, wie Kotlin an
die URL: ```/hello/{name}``` kommt?

Mit Reflektion muss die ```@Location```-Annotation der ```Hello```-Klasse ausgelesen werden. 
Das heißt, innerhalb der ```get```-Funktion muss *Ktor* auf das ```Hello```-Class-Objekt zugreifen, um an die
```@Location```-Annotation zu kommen. Da Kotlin auf der JVM die gleichen Einschränkungen hat wie Java, stehen die
generischen Typparameter zur Laufzeit nicht zur Verfügung (Type Erasure). In Java müsste man das Class-Objekt
explizit als Parameter übergeben:

    //Java Code
    get(Hello::class,  ...)

In Kotlin gibt es allerdings eine Möglichkeit, trotz *Type Erasure*  auf die generischen Parameter zuzugreifen:
'**inline reified**'. Der folgende Code zeigt einen Auszug der ```get```-Funktion:

    inline fun <reified T : Any> Route.get( ... ): Route {
        return location(T::class) {
            ...
        }
    }

Das Schlüsselwort ```inline``` sorgt dafür, dass der Code der Funktion an die Aufrufstelle kopiert wird und nicht wie
normalerweise aufgerufen wird. Durch das Schlüsselwort ```reified``` am Typparameter übergibt Kotlin zusätzlich das
ermittelte Class-Objekt an den kopierten Funktionsrumpf.  Dadurch ist der Zugriff auf ```T::class``` möglich und damit auch Reflektion.
Mit ```reified``` gelingt es sehr oft, die redundante Übergabe der Klasse als Parameter zu umgehen und den Code lesbarer zu gestalten.

## Interne Modularität durch Extension-Funktionen

Ist Ihnen im vorigen Abschnitt aufgefallen, dass die ```get```-Funktion als Extension-Funktion an der Klasse ```Route```
implementiert wurde?

    package io.ktor.locations

    inline fun <reified T : Any> Route.get( ... ): Route 

Die Klasse ```Route``` ist bei *Ktor* im Package ```io.ktor.routing``` umgesetzt. Die Klasse ermöglicht es,
zu einem URL-String einen Request-Handler zu registrieren.

Die Definition des Routings durch eine annotierte Klasse ist dagegen eine Erweiterung, die in einem separaten Modul
implementiert wird: ```io.kto.locations```

Diese interne Modularität wird durch *Extension-Funktionen* sehr elegant, ohne Vererbung, ohne explizite Delegation
und ohne vorbereitende Maßnahmen (*Plugins* etc) an der ```Route```-Klasse ermöglicht.

## Kodein - Dependency Injection mit Kotlin

Die nächsten beiden Sprach-Features möchte ich mit Hilfe von [Kodein](https://github.com/Kodein-Framework/Kodein-DI) vorstellen.
Kodein ist ein Dependency-Injection-Framework für Kotlin.

Den meisten wird bei Dependency Injection [Spring](http://spring.io/) einfallen. Natürlich kann man auch Spring mit
Kotlin nutzen. Seit der neuesten Version *'Spring 5'* gibt es sogar schon eingebaute
[Extension-Funktionen](https://docs.spring.io/spring/docs/5.0.0.BUILD-SNAPSHOT/spring-framework-reference/kotlin.html),
um die Möglichkeiten von Kotlin noch besser auszunutzen.

Kodein fokussiert in Gegensatz zu Spring ausschließlich auf Dependency Injection und nutzt die Sprach-Features von Kotlin
noch weiter aus. Wie in allen Dependency-Injection-Frameworks gibt es in *Kodein* eine Möglichkeit, Objekte als
Dependency-Kandidaten zu definieren. Dazu nutzt *Kodein* das schon bekannte Builder-Pattern mit einem *'Lambda with Receiver'*-Block.
Innerhalb des Lambdas ist der ```this```-Zeiger vom Typ ```Kodein.MainBuilder```. Dadurch kann man die API für die Registrierung
der Dependency-Kandidaten separieren von der späteren Benutzung der Dependencies:

    // Dependency-Kandidaten konfigurieren
    val kodein = Kodein { // this ist Kodein.MainBuilder
        // hier werden Kandidaten registriert
        ...
    }

    //hier kann auf die Kandidaten zugegriffen werden

Im folgenden Beispiel sieht man die beiden Funktionsaufrufe ```constant("dburl")``` und ```bind<DataSource>()```.
Diese werden an der Klasse ```Kodein.MainBuilder``` aufgerufen. Die erste Funktion registriert eine Konstante mit den
Namen ```dburl``` und die zweite Funktion ein Singleton vom Typ ```DataSource```:


    // Dependency-Kandidaten konfigurieren
    val kodein = Kodein { // this ist Kodein.MainBuilder
        constant("dburl") with "jdbc:h2:mem:singleton"

        bind<DataSource>() with singleton {
            JdbcDataSource().apply {
                setURL(instance("dburl"))
            }
        }
    }


## Infix-Funktionen vermeiden Klammern

Vielleicht ist Ihnen schon aufgefallen, dass nach den Funktionsaufrufen ```constant``` und  ```bind``` das Wort ```with``` auftaucht.
Das ist kein Schlüsselwort der Sprache *Kotlin*, sondern eine Funktion, die als ```infix``` definiert wurde.
*Infix*-Funktionen werden ähnlich wie Operatoren (z.B. ```+```) zwischen die Argumente ohne Punkt und ohne Klammern geschrieben.
    
Im nächsten Code-Block sehen Sie, wie die ```with```-Funktion als normaler Aufruf aussehen würde:

    bind<DataSource>().with(singleton {
        JdbcDataSource().apply {
            setURL(instance("dburl"))
        }
    }) 

An diesem Beispiel kann man gut sehen, dass *Infix*-Funktionen weniger Klammern brauchen und damit zur besseren
Lesbarkeit des Codes beitragen können.

Die Definition der ```with```-Funktion sieht etwas vereinfacht so aus:

    infix fun TypeBinder.with(binding: KodeinBinding<...>) = ...

Eine Infix-Funktion unterscheidet sich nur durch das Schlüsselwort ```infix``` von einer normalen Funktion und
Infix-Funktionen müssen immer genau einen Parameter haben.

Der Zugriff auf die registrierten Dependency-Kandidaten kann durch die ```instance```-Funktion erfolgen.
Diese ```instance```-Funktion ist als ```inline reified``` definiert und nutzt den generischen Typ als impliziten Parameter:

    // Dependencies nutzen
    val datasource  = kodein.direct.instance<DataSource>()


Die ```instance```-Funktion ist nicht direkt beim ```Kodein```-Interface definiert, sondern beim ```DKodein```-Interface.
Deswegen ist die Indirektion über das ```direct```-Property notwendig. Der Grund für Trennung der Interfaces und den
größeren Aufwand beim Zugriff auf die Dependency ist, dass der direkte Zugriff mittels ```instance```-Funktion nicht
der bevorzugte Weg ist. *Kodein* ist ein Dependency-**Injection**-Framework, bisher haben wir aber noch gar nicht gesehen,
wie Abhängigkeiten injiziert werden.

## Delegated Properties 

Sehen wir uns Dependency Injection in *Kodein* an einem klassischen Beispiel an. Es gibt einen ```DatabaseService```,
der zum Arbeiten sowohl die ```dbUrl``` als auch die ```dataSource``` benötigt:

    class DatabaseService(override val kodein: Kodein) : KodeinAware {
        val dbUrl: String by instance("dburl")
        val dataSource: DataSource by instance()   

        ...     
    }

Beginnen wir mit den beiden Properties. Diese werden mit ```by instance(...)``` initialisiert.
In diesem Fall handelt es sich bei ```by``` tatsächlich um ein Schlüsselwort in *Kotlin*, welches bei der Initialisierung
von Properties verwendet werden kann. Normalerweise generiert *Kotlin* für alle Properties einfache ```get```- und ggf. ```set```-Methoden.
Mit dem Schlüsselwort ```by``` kann man ein Objekt definieren, welches als *Delegate* benutzt wird. Das heißt,
immer wenn ein ```get```-Zugriff auf ein solches Property erfolgt, wird der Aufruf an das Delegation-Objekt weitergeleitet.

Damit man im ```DatabaseService``` den Delegation-Mechanismus für die Auflösung der Abhängigkeiten nutzen kann, muss der Service
das ```KodeinAware```-Interface implementieren. Dieses Interface definiert ein abstraktes ```kodein```-Property,
welches in Subklassen überschrieben werden muss:

    interface KodeinAware {
        val kodein: Kodein
        ...
    }

Im Falle des ```DatabaseService``` wird das Property gleich im Konstruktor übergeben und definiert:

    class DatabaseService(override val kodein: Kodein) : KodeinAware {
        ...

*Kodein* definiert für das ```KodeinAware```-Interface eine *Extension Funktion*: ```instance```. Diese dient zum
Erzeugen des speziellen Delegation-Objekts vom Typ ```KodeinProperty```:

    inline fun <reified T : Any> KodeinAware.instance(tag: Any? = null): KodeinProperty<T> = ...

Die ```instance```-Funktion hat Zugriff auf das ```kodein```-Property und zusätzlich  durch ```inline reified``` Zugriff
auf den generischen Typparameter - den Typ des zu initialisierenden Properties.
Beide Informationen werden an das neu erzeugte ```KodeinProperty```-Objekt übergeben. Erfolgt nun der Zugriff auf ein
solches *Delegated Property*, kann das ```KodeinProperty```-Objekt in der ```kodein```-Instanz nach dem geeigneten Dependency-Kandidaten suchen:

     val dataSource: DataSource by instance()   

Wenn Sie ein eigenes Delegation-Objekt implementieren wollen, dann finden Sie
[hier](https://kotlinlang.org/docs/reference/delegated-properties.html) die Details.

Der ```DatabaseService``` kann anschließend einfach erzeugt werden und benötigt nur eine ```kodein```-Instanz:

    val kodein = Kodein { 
        constant("dburl") with ...
        bind<DataSource>() with ...
    }

    val databaseService = DatabaseService(kodein)

## Zusammenspiel von *Delegated Properties*, *Lambdas with Receiver* und *Reified*

Im vorigen Abschnitt bekam der ```DatabaseService``` eine ```kodein```-Instanz im Konstruktor übergeben.
Das koppelt diesen Service stark an das verwendete Dependency-Injection-Framework. Auch das Testen wird schwieriger,
da immer eine *Kodein*-Instanz erzeugt werden muss. Im Folgenden soll gezeigt werden, wie es anders gehen kann und
gleichzeitig sehen wir ein interessantes Zusammenspiel von *Delegated Properties*, *Lambda with Receiver* und *Reified*.
Beginnen wir mit einer neuen Version des ```DatabaseService```:

    class DatabaseService(val dataSource: DataSource, val dbUrl: String) {
        ...
    }

In diesem Fall bekommt der ```DatabaseService``` die benötigten Daten direkt im Konstruktor übergeben. Das Erzeugen
des Services mit Depndency Injection würde dann so aussehen:

    val kodein = Kodein { 
        constant("dburl") with ...
        bind<DataSource>() with ...
    }

    val databaseService by kodein.newInstance { // this ist DKodein
        DatabaseService(instance(), instance("dburl"))
    }

Die ```kodein```-Instanz ist wieder mit der ```dburl``` und der ```DataSource``` konfiguriert worden. Zum Erzeugen nutzen
wir auch wieder ein *Delegated Property* mit dem ```by```-Schlüsselwort. Dazu wird die Funktion ```newInstance``` am
```kodein```-Objekt aufgerufen. Als Parameter wird ein *Lambda with Receiver* übergeben. In diesem Lambda ist der
```this```-Zeiger auf eine ```DKodein```-Instanz gesetzt. Diese stellt die ```instance```-Funktion für den Zugriff auf
die konfigurierten Objekte bereit. Da die ```instance```-Funktion wieder ```inline reified``` ist, kann auf alle expliziten
Typparameter verzichtet werden. Das Ergebnis des Lambda-Block wird der Variablen ```databaseService``` zugewiesen.

Ich finde es beeindruckend, dass der Datentyp ```DatabaseService``` nur ein einziges Mal explizit hingeschrieben werden muss
und die Datentypen für die ```dbUrl``` und die ```DataSource``` gar nicht explizit auftauchen.

## Alles geht, nichts muss!

Ich habe in dem Artikel bewusst einige komplexere Sprach-Features von Kotlin vorgestellt. Es soll kein Ansporn sein, alle
diese Features in Ihrer Anwendung zu benutzen. Richtig angewendet - bei eigenen Basis-Bibliotheken und eigenen DSLs - erlauben
diese Features interessante Konstruktionen und können zu "schönen" kompakten fachlichen Code führen.
Falsch oder übermässig angewendet können diese Features aber auch zu unverständlichen und komplexen Code führen.

Wer noch tiefer in das Thema einsteigen will, sollte sich die Möglichkeit
[Operatoren zu überladen](https://kotlinlang.org/docs/reference/keyword-reference.html) und
[lokale Extension-Funktionen](https://kotlinlang.org/docs/reference/extensions.html#declaring-extensions-as-members) anschauen.
Beides kann man ganz gut im Projekt [React Kotlin](https://github.com/JetBrains/create-react-kotlin-app) in Aktion sehen.