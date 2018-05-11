package de.e2.creative.kodein

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import javax.sql.DataSource

fun main(args: Array<String>) {
    val kodein = Kodein {
        constant("dburl") with "jdbc:h2:mem:singleton"

        bind<DataSource>() with singleton {
            JdbcDataSource().apply {
                setURL(instance("dburl"))
            }
        }
    }

    val datasource = kodein.direct.instance<DataSource>()
    datasource.connection.use { conn ->
        println(conn.metaData.url)
    }

    val kodeinWithoutInfix = Kodein {
        constant("dburl") with "jdbc:h2:mem:singleton"

        bind<DataSource>().with(singleton {
            JdbcDataSource().apply {
                setURL(instance("dburl"))
            }
        })
    }
}