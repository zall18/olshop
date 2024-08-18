package com.example.olshop

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.sql.Connection


class home : Fragment() {
    lateinit var productAdapter: ProductAdapter
    lateinit var data: MutableList<ProductModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var gridView = view.findViewById<GridView>(R.id.gridlayout)
        var connection = connection()
        data = mutableListOf<ProductModel>()

        lifecycleScope.launch {
            var result = getRequest(connection.Connection + "products", null)

            result.fold(
                onSuccess = {
                    response -> var jsonArray = JSONArray(response)

                    for (i in 0 until jsonArray.length())
                    {
                        var jsonObject = jsonArray.getJSONObject(i)

                        data.add(ProductModel(jsonObject.getString("id"), jsonObject.getString("title"), jsonObject.getString("price"), jsonObject.getString("category"), jsonObject
                            .getString("description"), jsonObject.getString("image")))
                    }

                    productAdapter = ProductAdapter(requireContext(), data)
                    gridView.adapter = productAdapter
                },
                onFailure = {
                    error -> error.printStackTrace()
                }
            )
        }
        
        var notit = view.findViewById<ImageView>(R.id.notification)
        notit.setOnClickListener {

            Toast.makeText(requireContext(), "this feature avaible soon", Toast.LENGTH_SHORT).show()
        }

        var search = view.findViewById<androidx.appcompat.widget.SearchView>(R.id.search)
        search.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                productAdapter.filter.filter(newText)
                return true
            }

        })

        var cart = view.findViewById<ImageView>(R.id.cart)
        cart.setOnClickListener {
            startActivity(Intent(requireContext(), Cart::class.java))
        }

        var option = view.findViewById<ImageView>(R.id.option)
        val drawerLayout = view.findViewById<DrawerLayout>(R.id.drawer_layout)
        option.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }
}