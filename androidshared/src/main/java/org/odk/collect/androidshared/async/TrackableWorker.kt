package org.odk.collect.androidshared.async

import org.odk.collect.androidshared.livedata.MutableNonNullLiveData
import org.odk.collect.androidshared.livedata.NonNullLiveData
import org.odk.collect.async.Scheduler
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier

class TrackableWorker(private val scheduler: Scheduler) {

    private val _isWorking = MutableNonNullLiveData(false)
    val isWorking: NonNullLiveData<Boolean> = _isWorking

    private var counter = AtomicInteger(0)

    fun <T> immediate(background: Supplier<T>, foreground: Consumer<T>) {
        counter.incrementAndGet()
        _isWorking.value = true
        scheduler.immediate(background) { result ->
            if (counter.decrementAndGet() == 0) {
                _isWorking.value = false
            }
            foreground.accept(result)
        }
    }

    fun immediate(background: Runnable) {
        immediate(
            background = {
                background.run()
            },
            foreground = {}
        )
    }
}
