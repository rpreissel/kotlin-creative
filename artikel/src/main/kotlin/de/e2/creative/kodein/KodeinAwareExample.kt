package de.e2.creative.kodeinaw

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import org.kodein.di.newInstance
import javax.sql.DataSource


class DatabaseService(override val kodein: Kodein) : KodeinAware {
    //Delegation Properties
    val dataSource: DataSource by instance()
    val dbUrl: String by instance("dburl")
}

class DatabaseService2(val dataSource: DataSource, val dbUrl: String) {
}

fun main(args: Array<String>) {

    val kodein = Kodein {
        constant("dburl") with "jdbc:h2:mem:singleton"
        bind<DataSource>() with singleton { JdbcDataSource().apply { setURL(instance("dburl")) } }
    }

    val databaseService = DatabaseService(kodein)
    println(databaseService.dbUrl)

    val databaseService2 by kodein.newInstance {
        DatabaseService2(instance(), instance("dburl"))
    }
}

