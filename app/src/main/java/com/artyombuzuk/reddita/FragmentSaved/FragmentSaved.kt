package com.artyombuzuk.reddita.FragmentSaved

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.artyombuzuk.reddita.FragmentSaved.comments.SavedCommentsFragmentDirections
import com.artyombuzuk.reddita.FragmentSaved.posts.SavedPostsFragmentDirections
import com.artyombuzuk.reddita.R
import com.artyombuzuk.reddita.databinding.FragmentSavedBinding
import com.artyombuzuk.reddita.others.viewBinding.ViewBindingFragment
import com.google.android.material.tabs.TabLayout

class FragmentSaved :
    ViewBindingFragment<FragmentSavedBinding>(FragmentSavedBinding::inflate) {
    private val viewModel: FragmentSavedViewModel by viewModels()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController by lazy {
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.saved_fragment_container)
            navHostFragment?.findNavController()
        }
        val currentTab = binding.savedTabLayout.getTabAt(viewModel.getState())
        currentTab?.select()

        binding.savedTabLayout.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val currentTabPosition = viewModel.getState()
                if (tab?.position == 0 && currentTabPosition == 1) {
                    viewModel.setState(0)
                    navController?.navigate(SavedCommentsFragmentDirections.actionSavedCommentsFragmentToSavedPostsFragment())
                }
                if (tab?.position == 1 && currentTabPosition == 0) {
                    viewModel.setState(1)
                    navController?.navigate(SavedPostsFragmentDirections.actionSavedPostsFragmentToSavedCommentsFragment())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}

        })
    }
}