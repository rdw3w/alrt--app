package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailyProgress
import com.example.data.model.MorningChecklistItem
import com.example.data.model.RoutineTask
import com.example.data.model.StudySession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        RoutineTask::class,
        MorningChecklistItem::class,
        StudySession::class,
        DailyProgress::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RudraDatabase : RoomDatabase() {
    abstract fun routineDao(): RoutineDao
    abstract fun morningChecklistDao(): MorningChecklistDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun dailyProgressDao(): DailyProgressDao

    companion object {
        @Volatile
        private var INSTANCE: RudraDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): RudraDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RudraDatabase::class.java,
                    "rudra_study_life_pro.db"
                )
                    .addCallback(RudraDatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class RudraDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database)
                    }
                }
            }
        }

        suspend fun populateInitialData(database: RudraDatabase) {
            database.routineDao().insertTasks(DefaultData.getDefaultRoutine())
            database.morningChecklistDao().insertItems(DefaultData.getDefaultMorningChecklist())
        }
    }
}
