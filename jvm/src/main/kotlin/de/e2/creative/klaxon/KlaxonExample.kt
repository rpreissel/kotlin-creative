package de.e2.creative.klaxon

import com.beust.klaxon.JsonArray
import com.beust.klaxon.Klaxon
import com.beust.klaxon.json

data class Person(
    val name: String,
    val age: Int
)

fun main(args: Array<String>) {
    val result: Person? = Klaxon().parse(
        """
    {
      "name": "Rene Preissel",
      "age": 45
    }
    """
    )

    println(result)

    val numberArray: JsonArray<Any?> = json {
        array(
            listOf(1, 2, 3).map {
                obj(it.toString() to it)
            }
        )
    }

    println(numberArray.toJsonString(prettyPrint = true))
}