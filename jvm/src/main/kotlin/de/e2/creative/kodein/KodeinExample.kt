package de.e2.creative.kodein

import org.h2.jdbcx.JdbcDataSource
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton
import org.kodein.di.generic.with
import java.security.SecureRandom
import java.util.*
import javax.sql.DataSource

fun main(args: Array<String>) {
    //Companion plus invoke fun
    //Lambda With Receiver
    val kodein = Kodein {
        //Reified Method
        //infix Method
        constant("dburl") with "jdbc:h2:mem:singleton"

        //Extension Method
        //Lambda With Receiver
        bind<DataSource>() with singleton {
            JdbcDataSource().apply {
                setURL(instance("dburl"))
            }
        }

        bind<Random>(tag = "random1") with provider { SecureRandom() }

        bind(tag = "random2") from provider { SecureRandom() }
    }

    //Reified
    val datasource: DataSource = kodein.direct.instance()
    datasource.connection.use { conn ->
        println(conn.metaData.url)
    }

    Kodein.direct {  }
}