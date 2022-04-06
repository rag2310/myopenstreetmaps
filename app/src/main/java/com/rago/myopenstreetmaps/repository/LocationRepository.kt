package com.rago.myopenstreetmaps.repository

import com.rago.myopenstreetmaps.service.SharedLocationManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val sharedLocationManager: SharedLocationManager
) {

    val receivingLocationUpdates: StateFlow<Boolean> = sharedLocationManager.receivingLocationUpdate

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLocations() = sharedLocationManager.locationFlow()
}