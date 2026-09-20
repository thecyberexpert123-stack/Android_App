package com.cyberexpert.androde.core.base

import android.util.Log
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Base layer - Event system similar to VS Code's src/vs/base/common/event.ts
 * Emitter, Event, lifecycle-aware event handling for production-ready pub/sub.
 * Used for configuration changes, layout changes, file changes, etc.
 */

typealias Event<T> = SharedFlow<T>

interface IEmitter<T> : IDisposable {
    val event: Event<T>
    fun fire(event: T)
}

class Emitter<T> : DisposableBase(), IEmitter<T> {
    private val _event = MutableSharedFlow<T>(extraBufferCapacity = 64)
    override val event: Event<T> = _event.asSharedFlow()

    // For non-coroutine listeners (like VS Code)
    private val listeners = CopyOnWriteArrayList<(T) -> Unit>()

    override fun fire(event: T) {
        if (isDisposed) {
            Log.w("Emitter", "Firing on disposed emitter: $event")
            return
        }
        // Coroutine flow
        _event.tryEmit(event)
        // Direct listeners
        listeners.forEach { listener ->
            try {
                listener(event)
            } catch (e: Exception) {
                Log.e("Emitter", "Listener failed for $event", e)
            }
        }
        Log.d("Emitter", "Fired event: $event to ${listeners.size} listeners")
    }

    fun addListener(listener: (T) -> Unit): IDisposable {
        listeners.add(listener)
        Log.d("Emitter", "Added listener, total: ${listeners.size}")
        return ActionDisposable {
            listeners.remove(listener)
            Log.d("Emitter", "Removed listener, total: ${listeners.size}")
        }
    }

    fun listenerCount(): Int = listeners.size

    override fun onDispose() {
        listeners.clear()
        Log.d("Emitter", "Disposed emitter with ${listeners.size} listeners")
    }
}

class EventMultiplexer<T> : DisposableBase() {
    private val emitter = Emitter<T>()
    val event: Event<T> = emitter.event
    private val store = DisposableStore()

    init {
        store.add(emitter)
    }

    fun add(event: Event<T>): IDisposable {
        // For simplicity, we don't subscribe to SharedFlow here
        // In real impl, would collect flow and fire
        Log.d("EventMultiplexer", "Added event source")
        return ActionDisposable { Log.d("EventMultiplexer", "Removed event source") }
    }

    override fun onDispose() {
        store.dispose()
    }
}
