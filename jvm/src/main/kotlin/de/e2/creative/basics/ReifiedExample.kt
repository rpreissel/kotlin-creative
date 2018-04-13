package de.e2.creative.basics

import com.beust.klaxon.Klaxon
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlin.reflect.KClass

class JacksonParser {
    val mapper: ObjectMapper = jacksonObjectMapper()

    fun <T : Any> parse(json: String, kClass: KClass<T>): T {
        return mapper.readValue(json, kClass.java)
    }
}

data class Person(val name: String)

fun main(args: Array<String>) {
    val jsonString = """
        {
            "name": "rene"
        }
        """
    val person1: Person? = JacksonParser().parse(jsonString, Person::class)
    funWithPerson(JacksonParser().parse(jsonString, Person::class))

    val person2: Person? = Klaxon().parse(jsonString)
    funWithPerson(Klaxon().parse(jsonString))

}

fun funWithPerson(person: Person?) {
    println(person)
}