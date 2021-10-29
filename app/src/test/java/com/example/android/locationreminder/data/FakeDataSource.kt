package com.example.android.locationreminder.data

import androidx.lifecycle.LiveData
import com.example.android.locationreminder.data.local.reminder.LocalReminderDataSource
import com.example.android.locationreminder.data.model.Reminder

class FakeDataSource: LocalReminderDataSource {

    override fun getReminders(): LiveData<List<Reminder>> {
        TODO("Not yet implemented")
    }

    override suspend fun insertReminder(reminder: Reminder) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteReminderById(reminderId: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getReminderById(id: String): Reminder {
        TODO("Not yet implemented")
    }
}