package de.e2.creative.html

import kotlinx.html.FlowOrInteractiveOrPhrasingContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.li
import kotlinx.html.stream.appendHTML
import kotlinx.html.ul

fun main(args: Array<String>) {
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


    System.out.appendHTML()
        .div {
            a("./link1") {
                h2 { text("Menu 1") }
            }
            a("./link2") {
                h2 { text("Menu 2") }
            }
        }

    System.out.appendHTML()
        .div {
            //Aufruf mit Named-Parameter macht den Code lesbarer
            menuEntry(title = "Menu 1", href = "./link1")
            menuEntry(title = "Menu 2", href = "./link2")
        }
}

fun FlowOrInteractiveOrPhrasingContent.menuEntry(title: String, href: String) =
    a(href) {
        h2 { text(title) }
    }