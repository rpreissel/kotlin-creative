package de.e2.creative.exposeddao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction

object Persons : IntIdTable() {
    val name = varchar("name", length = 50)
    val addressId = reference("address_id", Addresses).nullable()
}

class Person(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Person>(Persons)

    var name by Persons.name
    //Deleagte und Infix Funktion
    var address by Address optionalReferencedOn Persons.addressId
}

object Addresses : IntIdTable() {
    val city = varchar("city", 50)
}

class Address(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Address>(Addresses)

    var city by Addresses.city
}

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        create(Addresses, Persons)

        val hamburg = Address.new {
            //Override Setter with Delegate for dirty check
            city = "Hamburg"
        }

        val frankfurt = Address.new {
            city = "Frankfurt"
        }

        val mainz = Address.new {
            city = "Mainz"
        }

        Person.new {
            name = "Rene"
            address = hamburg
        }

        Person.new {
            name = "Jens"
            address = hamburg
        }

        Person.new {
            name = "Christian"
            address = mainz
        }

        val person3 = Person.new {
            name = "Markus"
            address = frankfurt
        }

        person3.name = "Marcus"

        Persons.deleteWhere { Persons.name like "%ian" }

        Person.find { Persons.addressId.isNotNull() }
            .forEach { p ->
                println("${p.name} wohnt in ${p.address?.city} ")
            }
    }
}