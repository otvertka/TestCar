package com.example.dda.testcar.room

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Car::class], version = 1)
abstract class CarDatabase : RoomDatabase() {

    abstract fun carDao(): CarDao

    companion object {
        @Volatile
        private var INSTANCE: CarDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CarDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {

                // create database
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CarDatabase::class.java,
                    "Car_database"
                )
                    .addCallback(CarDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }

        // при первом запуске заполняем БД
        private class CarDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.carDao())
                    }
                }
            }
        }

        fun populateDatabase(carDao: CarDao) {
            carDao.insert(Car("Audi", "audi1", 11000))
            carDao.insert(Car("Audi", "audi2", 11111))
            carDao.insert(Car("Lada", "lada1", 500))
            carDao.insert(Car("Lada", "lada2", 600))
            carDao.insert(Car("Lada", "lada3", 700))
            carDao.insert(Car("BMW", "bmw1", 12345))
            carDao.insert(Car("BMW", "bmw1", 13500))
            carDao.insert(Car("BMW", "bmw1", 15000))
            carDao.insert(Car("BMW", "bmw2", 5000))
            carDao.insert(Car("BMW", "bmw3", 30000))
        }
    }
}