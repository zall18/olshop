package com.example.olshop

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Connection
import java.sql.Date
import java.text.SimpleDateFormat

class detailProduk : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_produk)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var image: ShapeableImageView = findViewById(R.id.image_detail_product)
        var price: TextView = findViewById(R.id.price_detail_product)
        var name: TextView = findViewById(R.id.name_detail_product)
        var desc: TextView = findViewById(R.id.desc_detail_product)
        var connection = connection()
        var id = intent.getStringExtra("idproduct")

        lifecycleScope.launch {
            var result = getRequest(connection.Connection + "products/$id", null)

            result.fold(
                onSuccess = {
                    response -> var jsonObject = JSONObject(response)

                    if(!jsonObject.getString("id").isNullOrEmpty()){
                        MainScope().launch {
                            var bitmap = getImageFromUrl(jsonObject.getString("image"))
                            image.setImageBitmap(bitmap)
                        }

                        price.text = "Rp. " + jsonObject.getString("price")
                        name.text = jsonObject.getString("title")
                        desc.text = jsonObject.getString("description")
                    }
                },
                onFailure = {}

            )
        }

        var back: ImageView = findViewById(R.id.back_detail)
        back.setOnClickListener {
            startActivity(Intent(applicationContext, bottomNav::class.java))
        }

        var cart: AppCompatButton = findViewById(R.id.add_cart)
        cart.setOnClickListener {
            var dialog = BottomSheetDialog(this)
            var view = layoutInflater.inflate(R.layout.bottom_product, null)

            var image: ShapeableImageView = view.findViewById(R.id.image_bottom_product)
            var price: TextView = view.findViewById(R.id.price_bottom_product)
            var name: TextView = view.findViewById(R.id.name_bottom_product)
            var cart: AppCompatButton = view.findViewById(R.id.add_cart_bottom)
            var connection = connection()
            var id = intent.getStringExtra("idproduct")

            var add: ImageView = view.findViewById(R.id.add)
            var remove: ImageView = view.findViewById(R.id.remove)
            var jb: TextView = view.findViewById(R.id.jb)

            add.setOnClickListener {
                var hasil = Integer.parseInt(jb.text.toString()) + 1;
                jb.text = hasil.toString()
            }
            remove.setOnClickListener {
                var hasil = Integer.parseInt(jb.text.toString()) - 1;
                jb.text = hasil.toString()
            }


            lifecycleScope.launch {
                var result = getRequest(connection.Connection + "products/$id", null)

                result.fold(
                    onSuccess = {
                            response -> var jsonObject = JSONObject(response)

                        if(!jsonObject.getString("id").isNullOrEmpty()){
                            MainScope().launch {
                                var bitmap = getImageFromUrl(jsonObject.getString("image"))
                                image.setImageBitmap(bitmap)
                            }

                            price.text = "Rp. " + jsonObject.getString("price")
                            name.text = jsonObject.getString("title")
                        }
                    },
                    onFailure = {
                            error -> error.printStackTrace()
                    }

                )
            }

            cart.setOnClickListener {
                val date = java.util.Date()
                var current = SimpleDateFormat("yyyy-MM-dd").format(date)

                var jsonObject = JSONObject().apply {
                    put("productId", "$id")
                    put("quantity", jb.text.toString())
                }

                var jsonArray = JSONArray().apply {
                    put(jsonObject.toString())
                }

                var jsonObject2 = JSONObject().apply {
                    put("userId", "5")
                    put("date", current)
                    put("products", jsonArray)
                }

                lifecycleScope.launch {
                    var result = postRequest(connection.Connection + "carts", jsonObject, null)

                    result.fold(
                        onSuccess = { response -> var jsonObject3 = JSONObject(response)

                            if(!jsonObject3.getString("id").isNullOrEmpty())
                            {
                                Toast.makeText(applicationContext, "success to add cart", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()
                            }else{
                                Toast.makeText(applicationContext, "Login failed", Toast.LENGTH_SHORT).show()
                            }

                        },
                        onFailure = {
                                error -> error.printStackTrace()
                            Toast.makeText(applicationContext, "Login Failed", Toast.LENGTH_SHORT).show()
                        }

                    )
                }
            }
            dialog.setContentView(view)
            dialog.show()
        }
    }
}