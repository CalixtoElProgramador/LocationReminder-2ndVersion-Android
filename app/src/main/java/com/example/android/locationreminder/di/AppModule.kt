package com.example.android.locationreminder.di

import android.content.Context
import androidx.room.Room
import com.example.android.locationreminder.BaseApplication
import com.example.android.locationreminder.app.IoDispatcher
import com.example.android.locationreminder.app.MainDispatcher
import com.example.android.locationreminder.data.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRoomInstance(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "app_database").build()

    @Singleton
    @Provides
    fun provideApodDao(db: AppDatabase) = db.reminderDao

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context): BaseApplication =
        app as BaseApplication

    @IoDispatcher
    @Provides
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @MainDispatcher
    @Provides
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

}