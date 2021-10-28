package com.example.android.locationreminder.domain.reminder

import androidx.lifecycle.LiveData
import com.example.android.locationreminder.app.IoDispatcher
import com.example.android.locationreminder.data.local.reminder.LocalReminderDataSource
import com.example.android.locationreminder.data.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ReminderRepoImpl @Inject constructor(
    private val localDataSource: LocalReminderDataSource,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ReminderRepo {

    override fun getReminders(): LiveData<List<Reminder>> = localDataSource.getReminders()

    override suspend fun insertReminder(reminder: Reminder) = withContext(ioDispatcher) {
        localDataSource.insertReminder(reminder)
    }

    override suspend fun deleteReminderById(reminderId: String) = withContext(ioDispatcher) {
        localDataSource.deleteReminderById(reminderId)
    }

    override suspend fun getReminderById(id: String): Reminder = localDataSource.getReminderById(id)
}