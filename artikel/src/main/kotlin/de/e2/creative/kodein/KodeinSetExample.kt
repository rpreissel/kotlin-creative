package de.e2.creative.kodeinset

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.inSet
import org.kodein.di.generic.instance
import org.kodein.di.generic.setBinding
import org.kodein.di.generic.singleton
import javax.sql.DataSource

fun main(args: Array<String>) {
    val kodein = Kodein {
        bind() from setBinding<DataSource>()

        bind<DataSource>().inSet() with singleton { JdbcDataSource().apply { setURL("jdbc:h2:mem:db1") } }
        bind<DataSource>().inSet() with singleton { JdbcDataSource().apply { setURL("jdbc:h2:mem:db2") } }
        bind<DataSource>().inSet() with singleton { JdbcDataSource().apply { setURL("jdbc:h2:mem:db3") } }
    }

    val datasources: Set<DataSource> = kodein.direct.instance()
    for (datasource in datasources) {
        datasource.connection.use {conn ->
            println(conn.metaData.url)
        }
    }

}