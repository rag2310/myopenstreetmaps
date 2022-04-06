package com.rago.myopenstreetmaps.ui.map

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.rago.myopenstreetmaps.R
import com.rago.myopenstreetmaps.databinding.FragmentMapBinding
import com.rago.myopenstreetmaps.repository.LocationRepository
import com.rago.myopenstreetmaps.service.TimeRealLocationService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import javax.inject.Inject

@AndroidEntryPoint
class MapFragment : Fragment(), MapListener {

    private lateinit var binding: FragmentMapBinding
    private lateinit var map: MapView
    private lateinit var mapController: IMapController
    private var myLastLocation: GeoPoint? = null

    private var foregroundOnlyLocationServiceBound = false
    private var timeRealLocationService: TimeRealLocationService? = null
    private lateinit var sharedPreferences: SharedPreferences

    private val timeRealLocationServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as TimeRealLocationService.LocalBinder
            timeRealLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
            timeRealLocationService?.subscribeToLocationUpdates()
            subscribeToLocationUpdates()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            timeRealLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    @Inject
    lateinit var repository: LocationRepository
    private var locationFlow: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance()
            .load(requireContext(), PreferenceManager.getDefaultSharedPreferences(requireContext()))
        sharedPreferences =
            requireActivity().getSharedPreferences(
                getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
            )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false)
        map = binding.map
        map.setTileSource(TileSourceFactory.MAPNIK)
        mapController = map.controller
        map.setMultiTouchControls(true)
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        binding.centerCamera.setOnClickListener {
            myLastLocation?.let {
                mapController.setZoom(20.0)
                mapController.setCenter(it)
                mapController.animateTo(it)
            }
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
        map.minZoomLevel = 5.0
        map.addMapListener(this)
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onStart() {
        super.onStart()
        Intent(requireActivity(), TimeRealLocationService::class.java).also { intent ->
            requireActivity().bindService(
                intent,
                timeRealLocationServiceConnection,
                Context.BIND_AUTO_CREATE
            )
        }
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            requireActivity().unbindService(timeRealLocationServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        super.onStop()
    }

    private fun subscribeToLocationUpdates() {

        locationFlow = repository.getLocations()
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .onEach {
                val geoPoint = GeoPoint(it.latitude, it.longitude)
                if (myLastLocation != geoPoint) {
                    myLastLocation = geoPoint
                    mapController.setZoom(20.0)
                    mapController.setCenter(geoPoint)
                    mapController.animateTo(geoPoint)
                }
            }
            .launchIn(lifecycleScope)
    }

    private fun unsubscribeToLocationUpdates() {
        locationFlow?.cancel()
        timeRealLocationService?.unsubscribeToLocationUpdates()
    }

    companion object {
        private const val TAG = "MapFragment"
        private const val ZOOM = 20.0
    }

    override fun onScroll(event: ScrollEvent?): Boolean {
        event?.let {
            Log.i(TAG, "SCROLL x:${it.x} y:${it.y}")
            if (it.x != 0 && it.y != 0) {
                Log.i(TAG, "USERMOVE")
            } else {
                Log.i(TAG, "NOTUSERMOVE")
            }
        }
        return false
    }

    override fun onZoom(event: ZoomEvent?): Boolean {

        event?.let {
            Log.i(TAG, "ZOOM ${it.zoomLevel}")
            if (it.zoomLevel == ZOOM) {
                Log.i(TAG, "NOTUSERZOOM")
            } else {
                Log.i(TAG, "USERZOOM")
            }
        }
        return false
    }
}