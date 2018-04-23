package de.e2.creative.ktor

import io.ktor.html.Placeholder
import io.ktor.html.PlaceholderList
import io.ktor.html.Template
import io.ktor.html.TemplatePlaceholder
import io.ktor.html.each
import io.ktor.html.insert
import kotlinx.html.FlowContent
import kotlinx.html.FlowOrInteractiveOrPhrasingContent
import kotlinx.html.HTML
import kotlinx.html.HtmlBlockTag
import kotlinx.html.UL
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.h1
import kotlinx.html.h2
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.li
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import kotlinx.html.title
import kotlinx.html.ul

fun main(args: Array<String>) {
    System.out.appendHTML()
        .div {
            h1 {
                text("Features")
            }
            ul {
                li { text("Lambdas with receiver") }
                li { text("Extension Functions") }
                li { text("Refified") }
            }

            a("./link1") {
                h2 {
                    text("Menu 1")
                }
            }

            menuEntry(title = "Menu 1", href = "./link1")
            menuEntry(title = "Menu 2", href = "./link2")
        }

    val document = MulticolumnTemplate().run {
        column1 {
            +"Hello, Rene"
        }
        column2 {
            +"col2"
        }

        createHTML().html { apply() }
    }

    println(document)
}

fun FlowOrInteractiveOrPhrasingContent.menuEntry(title: String, href: String) =
    a(href) {
        h2 { text(title) }
    }

class MulticolumnTemplate(val main: MainTemplate = MainTemplate()) : Template<HTML> {
    val column1 = Placeholder<FlowContent>()
    val column2 = Placeholder<FlowContent>()
    override fun HTML.apply() {
        insert(main) {
            menu {
                item { +"One" }
                item { +"Two" }
            }
            content {
                div("column") {
                    insert(column1)
                }
                div("column") {
                    insert(column2)
                }
            }
        }
    }
}

class MainTemplate : Template<HTML> {
    val content = Placeholder<HtmlBlockTag>()
    val menu = TemplatePlaceholder<MenuTemplate>()
    override fun HTML.apply() {
        head() {
            title { +"Template" }
        }
        body {
            h1 {
                insert(content)
            }
            insert(MenuTemplate(), menu)
        }
    }
}

class MenuTemplate : Template<FlowContent> {
    val item = PlaceholderList<UL, FlowContent>()
    override fun FlowContent.apply() {
        if (!item.isEmpty()) {
            ul {
                each(item) {
                    li {
                        if (it.first) b {
                            insert(it)
                        } else {
                            insert(it)
                        }
                    }
                }
            }
        }
    }
}