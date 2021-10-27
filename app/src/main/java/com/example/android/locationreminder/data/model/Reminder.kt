package com.example.android.locationreminder.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "reminders")
data class Reminder @JvmOverloads constructor(
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "location") val location: String,
    @PrimaryKey @ColumnInfo(name = "entryid") val id: String = UUID.randomUUID().toString()
) {

    val titleForList: String
        get() = title

    val descriptionForList: String
        get() = description

    val locationForList: String
        get() = location

}
