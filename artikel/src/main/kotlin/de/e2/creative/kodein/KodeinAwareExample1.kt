package de.e2.creative.kodein.aware1

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import javax.sql.DataSource


class DatabaseService(override val kodein: Kodein) : KodeinAware {
    val dataSource: DataSource by instance()
    val dbUrl: String by instance("dburl")
}

fun main(args: Array<String>) {

    val kodein = Kodein {
        constant("dburl") with "jdbc:h2:mem:singleton"
        bind<DataSource>() with singleton { JdbcDataSource().apply { setURL(instance("dburl")) } }
    }

    val databaseService = DatabaseService(kodein)
    println(databaseService.dbUrl)
}

