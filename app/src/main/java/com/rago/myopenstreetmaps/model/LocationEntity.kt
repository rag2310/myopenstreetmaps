package com.rago.myopenstreetmaps.model

data class LocationEntity(
    var timestamp: Long = 0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var altitude: Double = 0.0,
    var indSend: Int = 0,
    var polyline: String = "",
    var speed: Double = 0.0,
    var bearing: Double = 0.0,
    var accuracy: Double = 0.0,
)
