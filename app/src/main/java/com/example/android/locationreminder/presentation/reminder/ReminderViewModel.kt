package com.example.android.locationreminder.presentation.reminder

import androidx.lifecycle.*
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.domain.reminder.ReminderRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(private val repo: ReminderRepo) : ViewModel() {

    private val _navigateToAddReminder = MutableLiveData<Boolean>()
    val navigateToAddReminder: LiveData<Boolean>
        get() = _navigateToAddReminder

    private val _navigateToAuth = MutableLiveData<Boolean>()
    val navigateToAuth: LiveData<Boolean>
        get() = _navigateToAuth

    private val _navigateToBack = MutableLiveData<Boolean>()
    val navigateToBack: LiveData<Boolean>
        get() = _navigateToBack

    val title = MutableLiveData<String>()
    val description = MutableLiveData<String>()
    val latitude = MutableLiveData<String>()
    val longitude = MutableLiveData<String>()

    private val _eventShowSnackbar = MutableLiveData<Boolean>()
    val eventShowSnackbar: LiveData<Boolean>
        get() = _eventShowSnackbar

    private val _eventReminderApproved = MutableLiveData<Boolean>()
    val eventReminderApproved: LiveData<Boolean>
        get() = _eventReminderApproved

    private val _navigateToMap = MutableLiveData<Boolean>()
    val navigateToMap: LiveData<Boolean>
        get() = _navigateToMap

    val reminders = repo.getReminders()

    val empty: LiveData<Boolean> = Transformations.map(reminders) { it.isEmpty() }

    private val _reminderClicked = MutableLiveData<Reminder?>()
    val reminderClicked: LiveData<Reminder?>
        get() = _reminderClicked

    private val _navigateToDetails = MutableLiveData<String?>()
    val navigateToDetails: LiveData<String?>
        get() = _navigateToDetails

    init {
        _navigateToAddReminder.value = false
        _navigateToAuth.value = false
        _navigateToBack.value = false
        _eventShowSnackbar.value = false
        _eventReminderApproved.value = false
        _navigateToMap.value = false
    }

    fun deleteReminder() = viewModelScope.launch {
        _reminderClicked.value?.let {
            repo.deleteReminderById(it.id)
            _eventShowSnackbar.value = true
            onBackArrowClick()
        }
    }

    fun onReminderClicked(reminder: Reminder) {
        _navigateToDetails.value = reminder.id
        _reminderClicked.value = reminder
    }

    fun onDetailNavigated() {
        _navigateToDetails.value = null
    }

    fun onListRemindersNavigated() {
        _reminderClicked.value = null
    }

    fun onCardMapClick() {
        _navigateToMap.value = true
    }

    fun onMapNavigated() {
        _navigateToMap.value = false
    }

    fun onReminderSaved() {
        _eventReminderApproved.value = false
    }

    fun clearInputs() {
        title.value = ""; description.value = ""; latitude.value = ""; longitude.value = ""
    }

    fun doneShowingSnackbar() {
        _eventShowSnackbar.value = false
    }

    fun onSaveReminderClick() {
        val title = title.value
        val description = description.value
        val latitude = latitude.value
        val longitude = longitude.value
        when {
            !title.isNullOrEmpty() && !description.isNullOrEmpty() && !latitude.isNullOrEmpty() && !longitude.isNullOrEmpty() ->
                createReminder(Reminder(title, description, "Coordinates: $latitude, $longitude"))
            else -> _eventShowSnackbar.value = true
        }
    }

    private fun createReminder(reminder: Reminder) = viewModelScope.launch {
        repo.insertReminder(reminder)
        _eventReminderApproved.value = true
    }

    fun onBackArrowClick() {
        _navigateToBack.value = true
    }

    fun onBackArrowNavigated() {
        _navigateToBack.value = false
    }

    fun onLogoutClick() {
        _navigateToAuth.value = true
    }

    fun onAuthNavigated() {
        _navigateToAuth.value = false
    }

    fun onAddReminderClick() {
        _navigateToAddReminder.value = true
    }

    fun onAddReminderNavigated() {
        _navigateToAddReminder.value = false
    }

}