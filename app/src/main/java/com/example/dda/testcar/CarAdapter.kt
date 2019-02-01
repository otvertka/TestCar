package com.example.dda.testcar

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.dda.testcar.room.Car

class CarAdapter internal constructor(context: Context) : RecyclerView.Adapter<CarViewHolder>(){

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var cars = emptyList<Car>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val itemView = inflater.inflate(R.layout.car_item, parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val current = cars[position]
        holder.bind(current)
    }

    internal fun setCars(cars: List<Car>){
        this.cars = cars
        notifyDataSetChanged()
    }

    fun getCarAt(position: Int): Car {
        return this.cars[position]
    }

    fun notifyData(){
        notifyDataSetChanged()
    }

    override fun getItemCount() = cars.size
}