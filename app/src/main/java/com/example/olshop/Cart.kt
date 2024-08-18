package com.example.olshop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class Cart : AppCompatActivity() {

    lateinit var cartAdapter: CartAdapter
    lateinit var data: MutableList<CartModel>
    lateinit var session: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        var listview: ListView = findViewById(R.id.listview)
        var connection = connection()
        data = mutableListOf<CartModel>()
        var dataCheckOut = mutableListOf<MutableList<String>>()
        session = getSharedPreferences("session", Context.MODE_PRIVATE)

        lifecycleScope.launch {
            var result = getRequest(connection.Connection + "carts/user/1", null)

            result.fold(
                onSuccess = {
                        response -> var jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length())
                    {
                        var jsonObject2 = jsonArray.getJSONObject(i)
                        Log.d("data", "onCreate: $jsonObject2")
                        var jsonArray2 = jsonObject2.getJSONArray("products")

                        for (a in 0 until jsonArray2.length())
                        {
                            var jsonObject3 = jsonArray2.getJSONObject(a)
                            println(jsonObject3)


                                var result = getRequest(connection.Connection + "products/" + jsonObject3.getString("productId"), null)

                                result.fold(
                                    onSuccess = {
                                            response -> var jsonObject4 = JSONObject(response)
                                        data.add(CartModel(jsonObject4.getString("id"), jsonObject4.getString("title"), jsonObject4.getString("price"), jsonObject4.getString("category"), jsonObject4
                                            .getString("description"), jsonObject4.getString("image"), jsonObject2.getString("date"), jsonObject3.getString("quantity")))
                                    },
                                    onFailure = {
                                            error -> error.printStackTrace()
                                    }

                                )

                        }
                    }
                    println(data.size)
                    var tb: TextView = findViewById(R.id.tb)
                    cartAdapter = CartAdapter(applicationContext, data, dataCheckOut, tb)
                    Log.d("adapter", "onCreate: $cartAdapter")
                    listview.adapter = cartAdapter
                },
                onFailure = {
                        error -> error.printStackTrace()
                }
            )
        }

        var back :ImageView = findViewById(R.id.back_cart)
        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }

        var bayar: TextView = findViewById(R.id.bayar)
        bayar.setOnClickListener {
            println(dataCheckOut)
            var intent = Intent(applicationContext, Invoice::class.java)
            var jsonArray = JSONArray()
            for (data in dataCheckOut) {
                var jsonObject = JSONObject().apply {
                    put("id", data[0])
                    put("title", data[1])
                    put("price", data[2])
                    put("category", data[3])
                    put("desc", data[4])
                    put("image", data[5])
                    put("date", data[6])
                    put("quantity", data[7])
                }
                jsonArray.put(jsonObject)
            }
            var jsonObject = JSONObject().apply {
                put("data", jsonArray)
            }
            intent.putExtra("data", jsonObject.getString("data").toString())

            startActivity(intent)
        }

    }
}