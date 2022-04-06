package com.rago.myopenstreetmaps.di

import android.content.Context
import com.rago.myopenstreetmaps.MyOpenStreetMapsApplication
import com.rago.myopenstreetmaps.service.SharedLocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSharedLocationManager(@ApplicationContext context: Context): SharedLocationManager =
        SharedLocationManager(context,
            (context.applicationContext as MyOpenStreetMapsApplication).applicationScope)
}