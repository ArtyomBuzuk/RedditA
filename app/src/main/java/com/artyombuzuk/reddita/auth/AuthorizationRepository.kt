package com.artyombuzuk.reddita.auth

import android.content.Context
import android.util.Log
import com.artyombuzuk.reddita.others.PermanentStorage

class AuthorizationRepository(private val context: Context) {
    private val sharedPrefs by lazy {
        context.getSharedPreferences(PermanentStorage.SharedPrefs.AUTH_PREFS,
            Context.MODE_PRIVATE)
    }

    fun toSharedPrefs() {
        Log.e("authorized", "shared")
        sharedPrefs.edit()
            .putBoolean("isAuthorized", true)
            .apply()
    }
}