package com.example.android.locationreminder.data.local.reminder

import androidx.lifecycle.LiveData
import com.example.android.locationreminder.data.model.Reminder

interface LocalReminderDataSource {

    fun getReminders(): LiveData<List<Reminder>>

    suspend fun insertReminder(reminder: Reminder)

    suspend fun deleteReminderById(reminderId: String)
}