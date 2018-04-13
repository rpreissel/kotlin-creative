package de.e2.creative.exposed

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Persons : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", length = 50)
    val addressId = integer("address_id") references Addresses.id
}

object Addresses : Table() {
    val id = integer("id").autoIncrement().primaryKey()
    val city = varchar("city", 50)
}

fun main(args: Array<String>) {
    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")

    transaction {
        create(Addresses, Persons)

        val hamburgId = Addresses.insert {
            it[city] = "Hamburg"
        } get Addresses.id

        val frankfurtId = Addresses.insert {
            it[city] = "Frankfurt"
        } get Addresses.id

        val mainzId = Addresses.insert {
            it[city] = "Mainz"
        } get Addresses.id

        val personId = Persons.insert {
            it[name] = "Rene"
            it[addressId] = hamburgId
        } get Persons.id
        Persons.insert {
            it[name] = "Jens"
            it[addressId] = hamburgId
        }

        Persons.insert {
            it[name] = "Christian"
            it[addressId] = mainzId
        }

        val person3Id = Persons.insert {
            it[name] = "Markus"
            it[addressId] = frankfurtId
        } get Persons.id

        //Lambda with expressions und lokale Extension-Funktionen
        Persons.update({ Persons.id eq person3Id }) {
            it[name] = "Marcus"
        }

        Persons.deleteWhere { Persons.name like "%ian" }

        val allNamesWithCities = (Persons innerJoin Addresses)
            .slice(Persons.name, Addresses.city)
            .selectAll()
        allNamesWithCities
            .forEach { row ->
                println("${row[Persons.name]} wohnt in ${row[Addresses.city]} ")
            }
    }
}