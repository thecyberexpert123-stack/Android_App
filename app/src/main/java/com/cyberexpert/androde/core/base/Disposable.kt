package com.cyberexpert.androde.core.base

import android.util.Log

/**
 * Base layer - Disposable pattern similar to VS Code's src/vs/base/common/lifecycle.ts
 * IDisposable, DisposableStore, lifecycle management for production-ready resource cleanup.
 * Used for listeners, observers, processes, etc. Prevents memory leaks.
 */
interface IDisposable {
    fun dispose()
    val isDisposed: Boolean
}

abstract class DisposableBase : IDisposable {
    override var isDisposed: Boolean = false
        private set

    override fun dispose() {
        if (!isDisposed) {
            isDisposed = true
            onDispose()
            Log.d("Disposable", "Disposed ${this::class.simpleName}")
        }
    }

    protected open fun onDispose() {}
}

class DisposableStore : DisposableBase() {
    private val disposables = mutableListOf<IDisposable>()

    fun add(disposable: IDisposable) {
        if (isDisposed) {
            disposable.dispose()
        } else {
            disposables.add(disposable)
        }
    }

    fun <T : IDisposable> addAndReturn(disposable: T): T {
        add(disposable)
        return disposable
    }

    override fun onDispose() {
        disposables.forEach {
            try {
                it.dispose()
            } catch (e: Exception) {
                Log.w("DisposableStore", "Failed to dispose", e)
            }
        }
        disposables.clear()
        Log.d("DisposableStore", "Cleared ${disposables.size} disposables")
    }

    fun clear() {
        disposables.forEach {
            try {
                it.dispose()
            } catch (e: Exception) {
                Log.w("DisposableStore", "Failed to dispose in clear", e)
            }
        }
        disposables.clear()
    }
}

class ActionDisposable(private val action: () -> Unit) : DisposableBase() {
    override fun onDispose() {
        try {
            action()
        } catch (e: Exception) {
            Log.w("ActionDisposable", "Action failed", e)
        }
    }
}

fun IDisposable.storeIn(store: DisposableStore): IDisposable {
    store.add(this)
    return this
}
