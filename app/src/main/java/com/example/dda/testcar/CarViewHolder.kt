package com.example.dda.testcar

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.dda.testcar.room.Car

class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val brand: TextView = itemView.findViewById(R.id.tvBrand)
    private val model: TextView = itemView.findViewById(R.id.tvModel)
    private val price: TextView = itemView.findViewById(R.id.tvPrice)
    private val image: ImageView = itemView.findViewById(R.id.imageBrand)

    fun bind(car: Car) {
        brand.text = car.brand
        model.text = car.model
        price.text = car.price.toString()
        when (car.brand) {
            "BMW" -> {
                image.setImageResource(R.mipmap.ic_bmw_round)
            }
            "Audi" -> {
                image.setImageResource(R.mipmap.ic_audi_round)
            }
            "Lada" -> {
                image.setImageResource(R.mipmap.ic_lada_round)
            }
            else -> {
            }
        }

    }
}