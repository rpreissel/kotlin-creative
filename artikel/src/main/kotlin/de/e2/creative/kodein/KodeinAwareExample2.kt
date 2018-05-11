package de.e2.creative.kodein.aware2

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import org.kodein.di.newInstance
import javax.sql.DataSource


class DatabaseService(val dataSource: DataSource, val dbUrl: String) {
}

fun main(args: Array<String>) {

    val kodein = Kodein {
        constant("dburl") with "jdbc:h2:mem:singleton"
        bind<DataSource>() with singleton { JdbcDataSource().apply { setURL(instance("dburl")) } }
    }

    val databaseService by kodein.newInstance {
        DatabaseService(instance(), instance("dburl"))
    }
    println(databaseService.dbUrl)

}

