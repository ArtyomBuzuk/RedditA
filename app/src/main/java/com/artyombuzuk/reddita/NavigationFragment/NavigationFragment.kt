package com.artyombuzuk.reddita.NavigationFragment

import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.artyombuzuk.reddita.databinding.NavigationFragmentBinding
import com.artyombuzuk.reddita.others.viewBinding.ViewBindingFragment

class NavigationFragment :
    ViewBindingFragment<NavigationFragmentBinding>(NavigationFragmentBinding::inflate) {
    override fun onStart() {
        super.onStart()
        val navController = Navigation.findNavController(binding.navFragment)
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
    }
}