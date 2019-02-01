package com.example.dda.testcar.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.room.*

@Dao
interface CarDao {

    @Update
    fun update(car: Car)

    @Insert
    fun insert(car: Car)

    @Delete
    fun delete(car: Car)

    @Query("DELETE FROM car_table")
    fun deleteAllCars()

    @Query("SELECT * from car_table")
    fun getAllCars(): LiveData<List<Car>>

    @Query("SELECT * from car_table where brand == :brand")
    fun getCarsByBrand(brand: String): LiveData<List<Car>>

    @Query("SELECT * from car_table where model == :model")
    fun getCarsByModel(model: String): LiveData<List<Car>>

    @RawQuery(observedEntities = [Car::class])
    fun getAllCarsViaRawQuery(guery: SupportSQLiteQuery): LiveData<List<Car>>

}