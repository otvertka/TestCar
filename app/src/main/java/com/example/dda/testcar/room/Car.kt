package com.example.dda.testcar.room

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "car_table")
data class Car(

    val brand: String,
    val model: String,
    val price: Int,

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)