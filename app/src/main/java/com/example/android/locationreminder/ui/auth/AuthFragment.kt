package com.example.android.locationreminder.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.locationreminder.R
import com.example.android.locationreminder.databinding.FragmentAuthBinding
import com.example.android.locationreminder.presentation.auth.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    private val viewModel by viewModels<AuthViewModel>()

    private lateinit var binding: FragmentAuthBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthBinding.bind(view)
        binding.authViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.navigationToLogin.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(AuthFragmentDirections.toRemindersFragment())
                viewModel.onListRemindersNavigated()
            }
        })

    }


}