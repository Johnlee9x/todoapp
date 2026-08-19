package com.tom.todoapp.core.ui

sealed class Async<out T> {
    object Loading : Async<Nothing>()

    data class Success<T>(val data: T) : Async<T>()
    data class Error(val errMsg: Int) : Async<Nothing>()
}
