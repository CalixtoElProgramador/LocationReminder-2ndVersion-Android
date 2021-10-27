package com.example.android.locationreminder.ui.reminder.add

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.locationreminder.R
import com.example.android.locationreminder.databinding.FragmentAddReminderBinding
import com.example.android.locationreminder.presentation.reminder.ReminderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddReminderFragment : Fragment(R.layout.fragment_add_reminder) {

    private val viewModel by activityViewModels<ReminderViewModel>()

    private lateinit var binding: FragmentAddReminderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAddReminderBinding.bind(view)
        binding.reminderViewModel = viewModel
        binding.lifecycleOwner = this

        binding.toolbarAddReminder.setNavigationOnClickListener { viewModel.onBackArrowClick() }

        viewModel.navigateToBack.observe(viewLifecycleOwner, {
            if (it) {
                activity?.onBackPressed()
                viewModel.onBackArrowNavigated()
                viewModel.clearInputs()
            }
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

}