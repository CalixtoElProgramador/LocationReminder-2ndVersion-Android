package com.example.android.locationreminder.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.android.locationreminder.R
import com.example.android.locationreminder.databinding.FragmentAuthBinding
import com.example.android.locationreminder.presentation.auth.AuthViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth) {

    companion object {
        private const val TAG = "AuthFragment"
    }

    private val viewModel by viewModels<AuthViewModel>()
    private val firebaseAut by lazy { FirebaseAuth.getInstance() }

    private lateinit var binding: FragmentAuthBinding
    private lateinit var registerForActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (firebaseAut.currentUser != null) {
            findNavController().navigate(AuthFragmentDirections.toRemindersFragment())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAuthBinding.bind(view)
        binding.authViewModel = viewModel
        binding.lifecycleOwner = this

        registerForSignInResult()

        viewModel.navigationToLogin.observe(viewLifecycleOwner, {
            if (it) { launchSignInFlow(); viewModel.onListRemindersNavigated() }
        })
    }

    private fun registerForSignInResult() {
        registerForActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){ result: ActivityResult ->
            val response = IdpResponse.fromResultIntent(result.data)
            // Listen to the result of the sign - in process
            if(result.resultCode == Activity.RESULT_OK){
                Log.i(TAG, "User ${FirebaseAuth.getInstance().currentUser?.displayName} has signed in")
                findNavController().navigate(AuthFragmentDirections.toRemindersFragment())
            } else {
                Log.i(TAG, "Sign in unsuccessful ${response?.error?.errorCode}")
            }
        }
    }

    private fun launchSignInFlow() {
        // Give users the option to sign in / register with their email
        // If users choose to register with their email,
        // they will need to create a password as well
        val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent.
        // We listen to the response of this activity with the
        // SIGN_IN_RESULT_CODE code
        registerForActivityResult.launch(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build())
    }

}