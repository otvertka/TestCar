package com.example.dda.testcar

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.Toast
import com.example.dda.testcar.room.Car
import kotlinx.android.synthetic.main.app_bar_layout.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val ADD_CAR_REQUEST = 1
        const val EDIT_CAR_REQUEST = 2
    }

    private lateinit var carViewModel: CarViewModel
    private lateinit var adapter: CarAdapter
    private lateinit var recyclerView: RecyclerView
    private val p = Paint()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@MainActivity, AddEditCarActivity::class.java)
            startActivityForResult(intent, ADD_CAR_REQUEST)
        }

        recyclerView = findViewById(R.id.recycler_view)
        adapter = CarAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.addOnItemTouchListener(
            RecyclerViewTouchListener(
                this,
                recyclerView,
                object : RecyclerViewTouchListener.ClickListener {
                    override fun onClick(view: View, position: Int) {
                        val intent = Intent(this@MainActivity, AddEditCarActivity::class.java)
                        intent.putExtra("id", adapter.getCarAt(position).id)
                        intent.putExtra("brand", adapter.getCarAt(position).brand)
                        intent.putExtra("model", adapter.getCarAt(position).model)
                        intent.putExtra("price", adapter.getCarAt(position).price)
                        startActivityForResult(intent, EDIT_CAR_REQUEST)
                    }

                    override fun onLongClick(view: View?, position: Int) {

                        val myBuilder = AlertDialog.Builder(this@MainActivity, R.style.myDialog)
                        myBuilder
                            .setTitle("Удаление авто")
                            .setMessage("Вы действительно хотите удалить это авто?")
                            .setPositiveButton("Delete") { _, _ ->
                                carViewModel.delete(adapter.getCarAt(position))
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                dialog.cancel()
                            }
                        val myDialog = myBuilder.create()
                        myDialog.show()
                    }
                })
        )

        carViewModel = ViewModelProviders.of(this).get(CarViewModel::class.java)
        carViewModel.allCars.observe(this, Observer { cars ->
            cars?.let { adapter.setCars(it) }
        })

        // этот метод работает не на всех устройствах.
        initSwipe()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_refresh -> {
                carViewModel.getCarsOrderByString("refresh").observe(this, Observer { cars ->
                    cars?.let { adapter.setCars(it) }
                })
            }
            R.id.action_price_down -> {
                carViewModel.getCarsOrderByString("priceASC").observe(this, Observer { cars ->
                    cars?.let { adapter.setCars(it) }
                })
            }
            R.id.action_price_up -> {
                carViewModel.getCarsOrderByString("priceDESC").observe(this, Observer { cars ->
                    cars?.let { adapter.setCars(it) }
                })
            }
            R.id.action_brand -> {
                showAlert("brand")
            }
            R.id.action_model -> {
                showAlert("model")
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)

        if (requestCode == ADD_CAR_REQUEST && resultCode == Activity.RESULT_OK) {
            intentData?.let {
                val car = Car(
                    it.getStringExtra("brand"),
                    it.getStringExtra("model"),
                    it.getIntExtra("price", 1)
                )
                carViewModel.insert(car)
            }
            Toast.makeText(applicationContext, "Car saved", Toast.LENGTH_LONG).show()
        } else if (requestCode == EDIT_CAR_REQUEST && resultCode == Activity.RESULT_OK) {
            val id = intentData?.getIntExtra("id", -1)

            if (id == -1) {
                Toast.makeText(applicationContext, "Car can't be updated", Toast.LENGTH_LONG).show()
                return
            }

            intentData?.let {
                val car = Car(
                    it.getStringExtra("brand"),
                    it.getStringExtra("model"),
                    it.getIntExtra("price", 1),
                    id
                )
                carViewModel.update(car)
                Toast.makeText(applicationContext, "Car updated", Toast.LENGTH_LONG).show()
            }
        } else {
            adapter.notifyData()
            Toast.makeText(applicationContext, "Car not saved", Toast.LENGTH_LONG).show()
        }
    }

    //вызываем диалоги для выбора фильтрации по производителю или модели
    private fun showAlert(brandOrModel: String) {
        val array: Array<String> = if (brandOrModel == "brand") {
            arrayOf("BMW", "Audi", "Lada")
        } else {
            arrayOf(
                "bmw1", "bmw2", "bmw3", "bmw4",
                "audi1", "audi2", "audi3", "audi4",
                "lada1", "lada2", "lada3", "lada4"
            )
        }

        val myBuilder = AlertDialog.Builder(this)
        myBuilder.setTitle("Выберите необходимое").setSingleChoiceItems(array, -1) { dialog: DialogInterface?, i: Int ->

            if (brandOrModel == "brand") {
                carViewModel.searchByBrand(array[i]).observe(this, Observer { cars ->
                    cars?.let { adapter.setCars(it) }
                })
            } else {
                carViewModel.searchByModel(array[i]).observe(this, Observer { cars ->
                    cars?.let { adapter.setCars(it) }
                })
            }
            dialog?.dismiss()
        }

        val myDialog = myBuilder.create()
        myDialog.show()
    }

    //свайп элемента для редактирования или удаления
    private fun initSwipe() {
        val simpleItemTouchCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition

                    if (direction == ItemTouchHelper.LEFT) {
                        carViewModel.delete(adapter.getCarAt(position))
                    } else {

                        val intent = Intent(this@MainActivity, AddEditCarActivity::class.java)
                        intent.putExtra("id", adapter.getCarAt(position).id)
                        intent.putExtra("brand", adapter.getCarAt(position).brand)
                        intent.putExtra("model", adapter.getCarAt(position).model)
                        intent.putExtra("price", adapter.getCarAt(position).price)
                        startActivityForResult(intent, EDIT_CAR_REQUEST)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val icon: Bitmap
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3

                        if (dX > 0) {
                            p.color = Color.parseColor("#388E3C")
                            val background = RectF(
                                itemView.left.toFloat(),
                                itemView.top.toFloat(),
                                itemView.left.toFloat() + dX,
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(resources, R.drawable.ic_edit_white)
                            val icon_dest = RectF(
                                itemView.left.toFloat() + width,
                                itemView.top.toFloat() + width,
                                itemView.left.toFloat() + 2 * width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        } else {
                            p.color = Color.parseColor("#D32F2F")
                            val background = RectF(
                                itemView.right.toFloat() + dX,
                                itemView.top.toFloat(),
                                itemView.right.toFloat(),
                                itemView.bottom.toFloat()
                            )
                            c.drawRect(background, p)
                            icon = BitmapFactory.decodeResource(resources, R.drawable.ic_delete_white)
                            val icon_dest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            c.drawBitmap(icon, null, icon_dest, p)
                        }
                    }
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

}

