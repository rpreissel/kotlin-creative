package de.e2.creative.coroutine

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.produce
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.selects.select
import java.util.*

fun main(args: Array<String>): Unit = runBlocking {
    //Lambda with Receiver und dadurch coroutineContext
    val asyncValue = async(coroutineContext) {
        delay(Random().nextInt(300))
        42
    }
    val producer = produce {
        val random = Random()
        //Lambda with Receiver State
        while (isActive) {
            delay(random.nextInt(300))
            //Lambda with receiver
            send(1)
        }
    }
    val channel = Channel<Int>()
    launch {
        delay(Random().nextInt(300))
        channel.receive()
    }


    val value = select<String> {
        //Operator overloading + Lambda with Receiver
        producer.onReceive { value ->
            "producer $value"
        }
        asyncValue.onAwait { value ->
            "async $value"
        }
        channel.onSend(10) {
            "send"
        }
        onTimeout(100) {
            "timeout"
        }
    }

    println(value)
}

