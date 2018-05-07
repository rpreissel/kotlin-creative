package de.e2.creative.ktor

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.locations.locations
import io.ktor.response.respondText
import io.ktor.response.respondWrite
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.title
import org.slf4j.event.Level

@Location("/")
class Index()

@Location("/hello/{name}")
class Hello(val name: String)

//Erzeugen einer Extension-Funktion um Zugriff auf Funktionen und Zustand zu bekommen
fun Application.main() {
    install(DefaultHeaders)
    install(Compression)
    install(Locations)
    //Companion als Factory, Configuration ..., Class als Verhalten
    //Lambda With Receiver für DSL
    install(CallLogging) {
        level = Level.INFO
    }
    install(StatusPages) {
        status(HttpStatusCode.NotFound) {
            call.respondText("${it.value} ${it.description}", status = it)
        }
        exception<Throwable> { cause ->
            call.respondText(cause::class.java.simpleName, status = HttpStatusCode.InternalServerError)
        }
    }


    install(Routing) {
        get("/lines/{times...}") {
            //Parameters ist keine Map, Operator [] wurde überladen
            val times = call.parameters["times"]?.toInt() ?: 1
            //Lambda with receiver für den Writer
            call.respondWrite {
                repeat(times) {
                    appendln("Line $it")
                }
            }
        }

        //reified Typ um zu Typisieren und Objekte anlegen zu können
        get<Index> {
            call.respondHtml {
                head {
                    title {
                        // Unary Operator zum Hinzufügen von Strings
                        // Extension nur innerhalb eines Tags sichtbar
                        text("Locations")
                    }
                }
                body {
                    h1 {
                        text("Greeter")
                    }
                    div {
                        a(locations.href(Hello("Rene"))) {
                            text("Say Hello")
                        }
                    }
                }
            }
        }

        get<Hello> { hello ->
            call.respondHtml {
                head {
                    title {
                        text("Hello")
                    }
                }
                body {
                    //Wieder Extension Funktion um DSL zu erweitern
                    h1 {
                        text("Hello ${hello.name}")
                    }
                }
            }
        }
    }

}

//Extension Funktion um DSL zu erweitern
fun FlowContent.greeter(greeterBody: FlowContent.() -> Unit) = h1 {
    greeterBody()
}

fun main(args: Array<String>) {
    val server = embeddedServer(
        Netty, port = 8080, module = Application::main
    )
    server.start(wait = true)
}