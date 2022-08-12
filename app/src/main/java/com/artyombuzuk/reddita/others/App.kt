package com.artyombuzuk.reddita.others

import android.app.Application
import com.artyombuzuk.reddita.DataBase.DataBase

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        DataBase.init(this)
    }
}