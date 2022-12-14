package ru.netology.nmedia.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.netology.nmedia.dao.PostDao
import javax.inject.Singleton

@InstallIn(SingletonComponent::class) // указ область действия
@Module // исполь в классах которые созд обьекты классов зависим
class DbModule {

    @Singleton
    @Provides
    fun provideDb(
        @ApplicationContext
        context: Context
    ) : AppDb = Room.databaseBuilder(context, AppDb::class.java, "app.db")
    //  .allowMainThreadQueries()
    .fallbackToDestructiveMigration()
    .build()

}