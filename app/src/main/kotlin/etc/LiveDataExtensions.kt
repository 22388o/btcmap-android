package etc

import androidx.lifecycle.LiveData
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.blockingObserve(): T {
    var value: T? = null
    val latch = CountDownLatch(1)

    observeForever {
        value = it
        latch.countDown()
    }

    latch.await(10, TimeUnit.SECONDS)

    if (latch.count != 0L) {
        throw TimeoutException()
    }

    @Suppress("UNCHECKED_CAST")
    return value as T
}

fun <T> LiveData<T>.toSingleEvent(): LiveData<T> {
    val result = LiveEvent<T>()
    result.addSource(this) {
        result.value = it
    }
    return result
}