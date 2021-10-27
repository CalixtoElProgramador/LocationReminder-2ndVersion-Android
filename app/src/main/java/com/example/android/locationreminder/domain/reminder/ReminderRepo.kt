package com.example.android.locationreminder.domain.reminder

import androidx.lifecycle.LiveData
import com.example.android.locationreminder.data.model.Reminder

interface ReminderRepo {

    fun getReminders(): LiveData<List<Reminder>>
    suspend fun insertReminder(reminder: Reminder)
    suspend fun deleteReminderById(reminderId: String)

}