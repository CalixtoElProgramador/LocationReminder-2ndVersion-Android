package com.example.android.locationreminder.presentation.reminder

import androidx.lifecycle.*
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.domain.reminder.ReminderRepo
import com.example.android.locationreminder.utils.createTitle
import com.google.android.gms.maps.model.PointOfInterest
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
    val location = MutableLiveData<String>()
    val latitude = MutableLiveData<String>()
    val longitude = MutableLiveData<String>()

    val selectedPOI = MutableLiveData<PointOfInterest?>()

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
    val isPoiSelected: LiveData<Boolean> = Transformations.map(selectedPOI) { it == null }

    private val _reminderClicked = MutableLiveData<Reminder?>()
    val reminderClicked: LiveData<Reminder?>
        get() = _reminderClicked

    private val _navigateToDetails = MutableLiveData<String?>()
    val navigateToDetails: LiveData<String?>
        get() = _navigateToDetails

    private val _eventInputsAreCorrect = MutableLiveData<Boolean>()
    val eventInputsAreCorrect: LiveData<Boolean>
        get() = _eventInputsAreCorrect

    init {
        _navigateToAddReminder.value = false
        _navigateToAuth.value = false
        _navigateToBack.value = false
        _eventShowSnackbar.value = false
        _eventReminderApproved.value = false
        _navigateToMap.value = false
        _eventInputsAreCorrect.value = false
        selectedPOI.value = null
    }

    fun verifyAgainInputs() {
        _eventInputsAreCorrect.value = false
    }

    fun onSavePoiSelected() {
        selectedPOI.value?.let { pointOfInterest ->
            latitude.value = pointOfInterest.latLng.latitude.toString()
            longitude.value = pointOfInterest.latLng.longitude.toString()
            _navigateToBack.value = true
        }
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
        location.value = selectedPOI.value?.name.toString()
        val latitude = latitude.value
        val longitude = longitude.value

        when {
            !title.isNullOrEmpty() && !description.isNullOrEmpty() && !latitude.isNullOrEmpty()
                    && !longitude.isNullOrEmpty() -> { _eventInputsAreCorrect.value = true }
            else -> { _eventShowSnackbar.value = true; verifyAgainInputs() }
        }
    }

    fun createReminder(reminder: Reminder) = viewModelScope.launch {
        repo.insertReminder(reminder)
        _eventReminderApproved.value = true
        selectedPOI.value = null
        verifyAgainInputs()
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