package com.rago.myopenstreetmaps.ui.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.viewModels
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.viewModel = mViewModel
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
        }
    }
}