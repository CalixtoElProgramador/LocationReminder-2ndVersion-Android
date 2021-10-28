package com.example.android.locationreminder.ui.reminder

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.android.locationreminder.R
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.presentation.reminder.ReminderViewModel
import com.example.android.locationreminder.ui.MainActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ReminderDataItem = "EXTRA_ReminderDataItem"

        //        receive the reminder object after the user clicks on the notification
        fun newIntent(context: Context, reminder: Reminder): Intent {
            val intent = Intent(context, DetailsActivity::class.java)
            intent.putExtra(EXTRA_ReminderDataItem, reminder)
            return intent
        }
    }

    private val viewModel by viewModels<ReminderViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)

        val reminderDetails = intent.getSerializableExtra(EXTRA_ReminderDataItem) as Reminder
        viewModel.onReminderClicked(reminderDetails)


    }
}