package com.example.android.locationreminder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.android.locationreminder.data.local.reminder.ReminderDao
import com.example.android.locationreminder.data.model.Reminder

/**
 * The Room Database that contains the Reminder table.
 *
 * Note that exportSchema should be true in production databases.
 */
@Database(entities = [Reminder::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract val reminderDao: ReminderDao

}