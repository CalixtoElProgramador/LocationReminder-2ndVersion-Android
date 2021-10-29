package com.example.android.locationreminder.di

import com.example.android.locationreminder.domain.reminder.ReminderRepo
import com.example.android.locationreminder.domain.reminder.ReminderRepoImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class ActivityModule {

    @Binds
    abstract fun bindReminderRepoImpl(repoImpl: ReminderRepoImpl): ReminderRepo

}