package com.example.dda.testcar.room

import android.arch.lifecycle.LiveData
import android.arch.persistence.db.SimpleSQLiteQuery

class CarRepository(private val carDao: CarDao) {

    val allCars: LiveData<List<Car>> = carDao.getAllCars()

    fun insert(car: Car) {
        carDao.insert(car)
    }

    fun update(car: Car) {
        carDao.update(car)
    }

    fun delete(car: Car) {
        carDao.delete(car)
    }

    fun searchByBrand(brand: String): LiveData<List<Car>> {
        return carDao.getCarsByBrand(brand)
    }

    fun searchByModel(model: String): LiveData<List<Car>> {
        return carDao.getCarsByModel(model)
    }

    fun getCarsOrderByString(query: String): LiveData<List<Car>> {
        val allCarsTEST: LiveData<List<Car>>
        val statement: String = when (query) {
            "priceASC" -> {
                "SELECT * from car_table order by price ASC"
            }
            "priceDESC" -> {
                "SELECT * from car_table order by price DESC"
            }
            else -> {
                "SELECT * from car_table"
            }
        }
        val query = SimpleSQLiteQuery(statement)
        allCarsTEST = carDao.getAllCarsViaRawQuery(query)
        return allCarsTEST
    }
}