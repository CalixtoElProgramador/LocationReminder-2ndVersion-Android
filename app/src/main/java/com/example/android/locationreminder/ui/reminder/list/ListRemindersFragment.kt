package com.example.android.locationreminder.ui.reminder.list

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.android.locationreminder.R
import com.example.android.locationreminder.databinding.FragmentListRemindersBinding
import com.example.android.locationreminder.presentation.reminder.ReminderViewModel
import com.example.android.locationreminder.ui.reminder.list.adapter.OnClickListener
import com.example.android.locationreminder.ui.reminder.list.adapter.RemindersAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ListRemindersFragment : Fragment(R.layout.fragment_list_reminders) {

    private val viewModel by activityViewModels<ReminderViewModel>()

    private lateinit var binding: FragmentListRemindersBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentListRemindersBinding.bind(view)
        binding.lifecycleOwner = this
        binding.reminderViewModel = viewModel

        viewModel.onListRemindersNavigated()

        binding.toolbarReminders.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> viewModel.onLogoutClick()
            }
            true
        }

        binding.recyclerReminders.adapter = RemindersAdapter(OnClickListener { reminder ->
            viewModel.onReminderClicked(reminder)
        })

        viewModel.navigateToDetails.observe(viewLifecycleOwner, {
            it?.let {
                findNavController().navigate(ListRemindersFragmentDirections.toDetailsReminderFragment())
                viewModel.onDetailNavigated()
            }
        })

        viewModel.navigateToAddReminder.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(ListRemindersFragmentDirections.toAddReminderFragment())
                viewModel.onAddReminderNavigated()
            }
        })

        viewModel.navigateToAuth.observe(viewLifecycleOwner, {
            if (it) {
                findNavController().navigate(ListRemindersFragmentDirections.toAuthFragment())
                viewModel.onAuthNavigated()
            }
        })

    }

    companion object {
        private const val TAG = "ListReminders"
    }

}