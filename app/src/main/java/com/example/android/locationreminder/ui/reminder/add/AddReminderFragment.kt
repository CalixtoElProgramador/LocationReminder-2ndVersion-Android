package com.example.android.locationreminder.ui.reminder.add

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.locationreminder.BuildConfig
import com.example.android.locationreminder.R
import com.example.android.locationreminder.app.Constants.Companion.ACTION_GEOFENCE_EVENT
import com.example.android.locationreminder.core.GeofenceBroadcastReceiver
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.databinding.FragmentAddReminderBinding
import com.example.android.locationreminder.presentation.reminder.ReminderViewModel
import com.example.android.locationreminder.utils.GeofencingConstants
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReminderFragment : Fragment(R.layout.fragment_add_reminder) {

    private val viewModel by activityViewModels<ReminderViewModel>()
    private val runningQOrLater =
        android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        intent.action = ACTION_GEOFENCE_EVENT
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private var inputsAreCorrect = false
    private lateinit var binding: FragmentAddReminderBinding
    private lateinit var geofencingClient: GeofencingClient

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddReminderBinding.bind(view)
        binding.reminderViewModel = viewModel
        binding.lifecycleOwner = this

        geofencingClient = LocationServices.getGeofencingClient(requireContext())

        binding.toolbarAddReminder.setNavigationOnClickListener { viewModel.onBackArrowClick() }
        binding.cardGoToMap.setOnClickListener { verifyLocationPermission() }

        viewModel.navigateToBack.observe(viewLifecycleOwner, {
            if (it) {
                activity?.onBackPressed()
                viewModel.onBackArrowNavigated()
                viewModel.clearInputs()
            }
        })

        viewModel.eventInputsAreCorrect.observe(viewLifecycleOwner, {
            if (it) { checkPermissionsAndStartGeofencing(); viewModel.verifyAgainInputs(); inputsAreCorrect = it }
        })


        viewModel.eventReminderApproved.observe(viewLifecycleOwner, {
            if (it) {
                activity?.onBackPressed()
                Toast.makeText(context, getString(R.string.successfully_saved), Toast.LENGTH_SHORT)
                    .show()
                viewModel.onReminderSaved()
                viewModel.clearInputs()
            }
        })

        viewModel.eventShowSnackbar.observe(viewLifecycleOwner, {
            if (it) {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    getString(R.string.error_inputs_empty),
                    Snackbar.LENGTH_SHORT
                ).setAnchorView(binding.fabAddReminder).show()
                viewModel.doneShowingSnackbar()
            }
        })

        viewModel.navigateToMap.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(AddReminderFragmentDirections.toMapFragment())
                viewModel.onMapNavigated()
            }
        })

    }

    private fun checkPermissionsAndStartGeofencing() {
        if (foregroundAndBackgroundLocationPermissionApproved()) {
            addGeofenceForClue()
        } else {
            requestForegroundAndBackgroundLocationPermissions()
        }
    }

    @TargetApi(29)
    private fun foregroundAndBackgroundLocationPermissionApproved(): Boolean {
        val foregroundLocationApproved = (
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ))
        val backgroundPermissionApproved =
            if (runningQOrLater) {
                PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(
                            requireContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION
                        )
            } else {
                true
            }
        return foregroundLocationApproved && backgroundPermissionApproved
    }

    private fun checkDeviceLocationSettingsAndStartGeofence(resolve: Boolean = true) {
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)

        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val locationSettingsResponseTask =
            settingsClient.checkLocationSettings(builder.build())

        locationSettingsResponseTask.addOnFailureListener { exception ->
            if (exception is ResolvableApiException && resolve) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_TURN_DEVICE_LOCATION_ON
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    Log.d(TAG, "Error geting location settings resolution: " + sendEx.message)
                }
            } else {
                Snackbar.make(
                    binding.root,
                    R.string.location_required_error, Snackbar.LENGTH_INDEFINITE
                ).setAction(android.R.string.ok) {
                    checkDeviceLocationSettingsAndStartGeofence()
                }.show()
            }
        }
        locationSettingsResponseTask.addOnCompleteListener {
            if (it.isSuccessful && inputsAreCorrect) {
                addGeofenceForClue()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeofenceForClue() {
        val title = viewModel.title.value.toString()
        val description = viewModel.description.value.toString()
        val location = viewModel.location.value.toString()
        val latitude = viewModel.latitude.value?.toDouble()
        val longitude = viewModel.longitude.value?.toDouble()

        val reminder = Reminder(title, description, location, latitude!!, longitude!!)

        val geofence = Geofence.Builder()
            .setRequestId(reminder.id)
            .setCircularRegion(
                reminder.latitude, reminder.longitude,
                GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .build()

        // Build the geofence request
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()
        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent).run {
            addOnSuccessListener {
                Log.d(TAG, "Added geofence ${geofence.requestId}")
            }
            addOnFailureListener {
                Toast.makeText(
                    requireActivity(),
                    R.string.geofences_not_added,
                    Toast.LENGTH_SHORT
                ).show()
                if ((it.message != null)) {
                    Log.w(TAG, it.message.toString())
                }
            }
        }
        viewModel.createReminder(reminder)
    }

    @TargetApi(29)
    private fun requestForegroundAndBackgroundLocationPermissions() {
        if (foregroundAndBackgroundLocationPermissionApproved())
            return

        // Else request the permission
        // this provides the result[LOCATION_PERMISSION_INDEX]
        var permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)

        val resultCode = when {
            runningQOrLater -> {
                // this provides the result[BACKGROUND_LOCATION_PERMISSION_INDEX]
                permissionsArray += Manifest.permission.ACCESS_BACKGROUND_LOCATION
                REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE
            }
            else -> REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
        }

        Log.d(TAG, "Request foreground only location permission")
        ActivityCompat.requestPermissions(
            requireActivity(),
            permissionsArray,
            resultCode
        )
    }

    private fun verifyLocationPermission() {
        if (isPermissionGranted()) {
            viewModel.onCardMapClick()
        } else {
            requestLocationPermission()
        }
    }

    private fun isPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        val permissionsArray = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        requestPermissions(permissionsArray, REQUEST_PERMISSION_LOCATION)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty() || grantResults[0] == PackageManager.PERMISSION_DENIED) {
            Snackbar.make(
                requireContext(),
                binding.root,
                getString(R.string.location_required_error),
                Snackbar.LENGTH_LONG
            )
                .setAnchorView(binding.fabAddReminder)
                .setAction(getString(R.string.settings)) {
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            verifyLocationPermission()
        }

        if (
            grantResults.isEmpty() ||
            grantResults[LOCATION_PERMISSION_INDEX] == PackageManager.PERMISSION_DENIED ||
            (requestCode == REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE &&
                    grantResults[BACKGROUND_LOCATION_PERMISSION_INDEX] ==
                    PackageManager.PERMISSION_DENIED)
        ) {
            // Permission denied.
            Snackbar.make(
                binding.root,
                R.string.permission_denied_explanation, Snackbar.LENGTH_LONG
            )
                .setAnchorView(binding.fabAddReminder)
                .setAction(R.string.settings) {
                    // Displays App settings screen.
                    startActivity(Intent().apply {
                        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }.show()
        } else {
            checkDeviceLocationSettingsAndStartGeofence()
        }

    }

}

private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
private const val LOCATION_PERMISSION_INDEX = 0
private const val BACKGROUND_LOCATION_PERMISSION_INDEX = 1
private const val REQUEST_FOREGROUND_AND_BACKGROUND_PERMISSION_RESULT_CODE = 33
private const val REQUEST_PERMISSION_LOCATION = 1000
private const val REQUEST_TURN_DEVICE_LOCATION_ON = 29
private const val TAG = "AddReminderFragment"