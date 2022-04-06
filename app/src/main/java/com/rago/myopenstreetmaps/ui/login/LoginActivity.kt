package com.rago.myopenstreetmaps.ui.login

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.rago.myopenstreetmaps.R
import com.rago.myopenstreetmaps.databinding.ActivityLoginBinding
import com.rago.myopenstreetmaps.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val mViewModel: LoginViewModel by viewModels()
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = mViewModel
        requestMultiplePermissions = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) {
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        createObs()
    }

    private fun createObs() {
        lifecycleScope.launchWhenCreated {
            launch {
                mViewModel.onLogin.collect(::stateOnLogin)
            }
        }
    }

    private fun stateOnLogin(state: Boolean) {
        if (state) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            if (!allPermissionsGranted())
                requestMultiplePermissions.launch(REQUIRED_PERMISSIONS_APP)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS_APP.all {
        ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val REQUIRED_PERMISSIONS_APP =
            mutableListOf(
                Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
                add(Manifest.permission.ACCESS_FINE_LOCATION)
                add(Manifest.permission.ACCESS_COARSE_LOCATION)
            }.toTypedArray()
    }
}