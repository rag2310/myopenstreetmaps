package com.rago.myopenstreetmaps

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.GlobalScope

@HiltAndroidApp
class MyOpenStreetMapsApplication : Application(){
    val applicationScope = GlobalScope
}