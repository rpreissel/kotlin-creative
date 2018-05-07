package de.e2.creative.kodeingaw

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.conf.KodeinGlobalAware
import org.kodein.di.conf.global
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import javax.sql.DataSource


class DatabaseService() : KodeinGlobalAware {
    //Delegation Properties
    val dataSource: DataSource by instance()
    val dbUrl: String by instance("dburl")
}

fun main(args: Array<String>) {

    Kodein.global.addConfig {
        constant("dburl") with "jdbc:h2:mem:singleton"
        bind<DataSource>() with singleton { JdbcDataSource().apply { setURL(instance("dburl")) } }
    }

    val datasource: DataSource = Kodein.global.direct.instance()

    val databaseService = DatabaseService()
    println(databaseService.dbUrl)
}

