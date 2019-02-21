package br.com.bruno.join.extensions

import androidx.databinding.Observable
import androidx.databinding.ObservableField

inline fun <R> ObservableField<R>.observe(crossinline callback: (R?) -> Unit) = object : Observable.OnPropertyChangedCallback() {
    override fun onPropertyChanged(observable: Observable?, i: Int) = callback(get())
}.also {
    addOnPropertyChangedCallback(it)
}.let {
    io.reactivex.disposables.Disposables.fromAction { removeOnPropertyChangedCallback(it) }
}


@Suppress("UNCHECKED_CAST")
inline fun <R> ObservableField<R>.rxObservable(): io.reactivex.Observable<R> = io.reactivex.Observable.create { subscriber ->
    object : Observable.OnPropertyChangedCallback() {
        override fun onPropertyChanged(observable: Observable, id: Int) = try {
            subscriber.onNext(this@rxObservable.get() as R)
        } catch (e: Exception) {
            subscriber.onError(e)
        }
    }.let {
        subscriber.setCancellable { this.removeOnPropertyChangedCallback(it) }
        this.addOnPropertyChangedCallback(it)
    }
}