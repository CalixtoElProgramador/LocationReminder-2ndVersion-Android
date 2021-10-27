package com.example.android.locationreminder.ui.reminder.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.fragment.app.activityViewModels
import com.example.android.locationreminder.R
import com.example.android.locationreminder.databinding.FragmentDetailsReminderBinding
import com.example.android.locationreminder.presentation.reminder.ReminderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsReminderFragment : Fragment(R.layout.fragment_details_reminder) {

    private val viewModel by activityViewModels<ReminderViewModel>()

    private lateinit var binding: FragmentDetailsReminderBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDetailsReminderBinding.bind(view)
        binding.lifecycleOwner = this
        binding.reminderViewModel = viewModel

        binding.toolbarDetailsReminder.setNavigationOnClickListener { viewModel.onBackArrowClick() }

        binding.toolbarDetailsReminder.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete -> viewModel.deleteReminder()
            }
            true
        }

        viewModel.eventShowSnackbar.observe(viewLifecycleOwner, {
            if (it) {
                Snackbar.make(
                    requireContext(),
                    binding.root,
                    getString(R.string.message_reminder_delete),
                    Snackbar.LENGTH_SHORT
                ).show()
                viewModel.doneShowingSnackbar()
            }
        })

        viewModel.navigateToBack.observe(viewLifecycleOwner, {
            if (it) {
                activity?.onBackPressed()
                viewModel.onBackArrowNavigated()
            }
        })

    }


}