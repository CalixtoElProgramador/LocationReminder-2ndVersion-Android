package com.example.android.locationreminder.core

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.JobIntentService
import com.example.android.locationreminder.R
import com.example.android.locationreminder.data.local.reminder.ReminderDao
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.utils.sendNotification
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@AndroidEntryPoint
class GeofenceTransitionsJobIntentService: JobIntentService(), CoroutineScope {

    @Inject
    lateinit var repo: ReminderDao

    private var coroutineJob: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573
        private const val TAG = "GeofenceTransitionsJob"

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.e(TAG, "Error: ${geofencingEvent.errorCode}")
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            Log.d(TAG, applicationContext.getString(R.string.geofence_entered))
            sendNotification(geofencingEvent.triggeringGeofences)
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            Log.d(TAG, "Inside geofence")
        }

    }

    private fun sendNotification(triggeringGeofences: List<Geofence>) {
        for (geofence in triggeringGeofences) {
            val requestId = geofence.requestId
            CoroutineScope(coroutineContext).launch(SupervisorJob()) {
                val result = repo.getReminderById(requestId)
                sendNotification(
                    this@GeofenceTransitionsJobIntentService, Reminder(
                        result.title,
                        result.description,
                        result.location,
                        result.latitude,
                        result.longitude,
                        result.id
                    )
                )
            }
        }
    }

}