package ru.netology.nmedia.application

import android.app.Application
import ru.netology.nmedia.auth.AppAuth

class NMediaApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        AppAuth.initAuth(this)


        //TODO: remove before release
      //  AppAuth.getInstance().setAuth(5, "x-token")
    }
}