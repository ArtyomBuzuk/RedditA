package com.artyombuzuk.reddita.main_fragment

import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.artyombuzuk.reddita.R
import com.artyombuzuk.reddita.databinding.FragmentMainBinding
import com.artyombuzuk.reddita.others.ConnectionReceiver
import com.artyombuzuk.reddita.others.PermanentStorage
import com.artyombuzuk.reddita.others.viewBinding.ViewBindingFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainFragment : ViewBindingFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {
    private val receiver = ConnectionReceiver()
    private val viewModel: MainViewModel by viewModels()
    private var job: Job? = null
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.isConnectedViaWifi()

        if (PermanentStorage.token != "") {
            viewModel.isAuthorized(true)
            findNavController().navigate(R.id.action_mainFragment_to_navigationFragment)
        } else {
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SUPPLICANT_CONNECTION_CHANGE_ACTION)
            requireContext().registerReceiver(receiver, intentFilter)

            if (PermanentStorage.connection) {
                viewModel.isAuthorized(false)
            } else {
                Toast.makeText(
                    requireContext(),
                    requireContext().getString(R.string.no_internet_connection),
                    Toast.LENGTH_LONG
                ).show()
                job = lifecycleScope.launch {
                    while (true) {
                        delay(200)
                        if (PermanentStorage.connection) {
                            viewModel.isAuthorized(false)
                            job!!.cancel()
                        }
                    }
                }
            }
        }

        viewModel.isAuthorized.observe(viewLifecycleOwner, ::isAuthorized)
        viewModel.isConnected.observe(viewLifecycleOwner, ::connectionStatus)
    }

    private fun isAuthorized(value: Pair<Boolean, Boolean>) {
        if (value.first) {
            if (!value.second) {
                viewModel.toSharedPrefs()
            }
        } else {
            findNavController().navigate(R.id.action_mainFragment_to_authorizationFragment)
        }
    }

    private fun connectionStatus(value: Boolean) {
        PermanentStorage.connection = value
    }
}