package com.tom.todoapp.util

sealed class Async<T> {
    object Loading : Async<Nothing>()

    data class Success<T>(val data: T) : Async<T>()
    data class Error(val errMsg: Int) : Async<Nothing>()
}