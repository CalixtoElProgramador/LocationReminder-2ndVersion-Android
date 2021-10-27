package com.example.android.locationreminder.data.local.reminder

import androidx.lifecycle.LiveData
import com.example.android.locationreminder.app.IoDispatcher
import com.example.android.locationreminder.data.model.Reminder
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalReminderDataSourceImpl @Inject constructor(
    private val reminderDao: ReminderDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocalReminderDataSource {

    override fun getReminders(): LiveData<List<Reminder>> = reminderDao.getReminders()

    override suspend fun insertReminder(reminder: Reminder) = withContext(ioDispatcher) {
        reminderDao.insertReminder(reminder)
    }

    override suspend fun deleteReminderById(reminderId: String) = withContext<Unit>(ioDispatcher) {
        reminderDao.deleteReminderById(reminderId)
    }


}