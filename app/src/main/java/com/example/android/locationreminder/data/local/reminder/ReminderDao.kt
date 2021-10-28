package com.example.android.locationreminder.data.local.reminder

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.locationreminder.data.model.Reminder

@Dao
interface ReminderDao {

    /**
     * Insert a reminder in the database. If the reminder already exists, replace it.
     *
     * @param reminder the reminder to be inserted.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder)

    /**
     * Observes list of reminders.
     *
     * @return all reminders.
     */
    @Query("SELECT * FROM Reminders")
    fun getReminders(): LiveData<List<Reminder>>

    /**
     * @param reminderId the id of the reminder
     * @return the reminder object with the reminderId
     */
    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM reminders where entryid = :reminderId")
    suspend fun getReminderById(reminderId: String): Reminder

    /**
     * Delete a task by id.
     *
     * @return the number of tasks deleted. This should always be 1.
     */
    @Query("DELETE FROM Reminders WHERE entryid = :reminderId")
    suspend fun deleteReminderById(reminderId: String): Int

}