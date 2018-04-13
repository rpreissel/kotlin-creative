package de.e2.creative.jooby

import org.jooby.Kooby

//Übergabe der Konfiguration im Konstruktor
class App: Kooby({
    //Lambda mit Receiver: this ist Request
    get("/") {
        val name = param("name").value("Kotlin")
        "Hello $name!"
    }
})

fun main(args: Array<String>) {
    org.jooby.run(::App, *args)
}