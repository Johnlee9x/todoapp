package com.tom.todoapp

import android.app.Application
import android.util.Log

class TodoApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i("tamld7", "onCreate: ")
    }
}