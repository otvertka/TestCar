package com.example.dda.testcar

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import com.example.dda.testcar.room.Car
import com.example.dda.testcar.room.CarDatabase
import com.example.dda.testcar.room.CarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class CarViewModel(application: Application) : AndroidViewModel(application) {

    private var parentJob = Job()
    private val coroutineContext: CoroutineContext
        get() = parentJob + Dispatchers.Main
    private val scope = CoroutineScope(coroutineContext)


    private val repository: CarRepository
    val allCars: LiveData<List<Car>>

    init {
        val carsDao = CarDatabase.getDatabase(application, scope).carDao()
        repository = CarRepository(carsDao)
        allCars = repository.allCars
    }

    fun insert(car: Car) = scope.launch(Dispatchers.IO) {
        repository.insert(car)
    }

    fun update(car: Car) = scope.launch(Dispatchers.IO) {
        repository.update(car)
    }

    fun delete(car: Car) = scope.launch(Dispatchers.IO) {
        repository.delete(car)
    }

    fun searchByBrand(brand: String): LiveData<List<Car>> {
        return repository.searchByBrand(brand)
    }

    fun searchByModel(model: String): LiveData<List<Car>> {
        return repository.searchByModel(model)
    }

    fun getCarsOrderByString(query: String): LiveData<List<Car>> {
        return repository.getCarsOrderByString(query)
    }

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

}