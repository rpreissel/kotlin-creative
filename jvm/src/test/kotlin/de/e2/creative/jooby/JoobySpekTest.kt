package de.e2.creative.jooby

import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import org.amshove.kluent.`should equal`
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.SpecBody
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jooby.Jooby
import org.jooby.Status

//Auch bei Spek Übergabe der Tests im Konstruktor
object AppTest : Spek({
    jooby(App()) {
        describe("Get /") {
            given("queryParameter name=Rene") {
                it("should return Hello Rene!") {
                    val name = "Rene"
                    given().queryParam("name", name)
                        .`when`().get("/")
                        .then().assertThat().statusCode(Status.OK.value())
                        .extract().asString()
                        .let {
                            //Kluent Infix Methods
                            it shouldEqual "Hello $name!"
                        }
                }
            }

            given("no queryParameter") {
                it("should return Kotlin as the default name") {
                    get("/")
                        .then()
                        .assertThat().statusCode(Status.OK.value())
                        .extract().asString()
                        .let {
                            //Kluent Infix plus backticks
                            it `should equal` "Hello Kotlin!"
                        }
                }
            }
        }
    }
})

//Extension-Funktionen zum Erweitern + Lambda wih Extension für die Konfiguration
fun SpecBody.jooby(app: Jooby, body: SpecBody.() -> Unit) {
    beforeGroup {
        app.start("server.join=false")
    }

    body()

    afterGroup {
        app.stop()
    }
}