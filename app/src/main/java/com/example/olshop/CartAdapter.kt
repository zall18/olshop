package com.example.olshop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class CartAdapter(var context: Context, var data: MutableList<CartModel>, var dataCheckOut: MutableList<MutableList<String>>, var total: TextView): BaseAdapter() {

    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var imageLoad = MainScope()
    var countData = 0.0

    override fun getCount(): Int {
        println(data.size)
        return data.size

    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView ?: inflater.inflate(R.layout.cartitem, null, false)

        var image = view.findViewById<ShapeableImageView>(R.id.image_cart)
        var name = view.findViewById<TextView>(R.id.name_cart)
        var price = view.findViewById<TextView>(R.id.price_cart)
        var check = view.findViewById<CheckBox>(R.id.checkCart)
        var qty = view.findViewById<TextView>(R.id.jb_cart)
        var add = view.findViewById<ImageView>(R.id.add_cart)
        var remove = view.findViewById<ImageView>(R.id.remove_cart)

        var product = getItem(position) as CartModel
        println("test")

        name.text = product.title
        price.text = "Rp. " + product.price.toString()
        qty.text = product.qty
        imageLoad.launch {
            var bitmap = getImageFromUrl(product.image)
            image.setImageBitmap(bitmap)
        }

        add.setOnClickListener {
            var hasil = Integer.parseInt(qty.text.toString()) + 1;
            qty.text = hasil.toString()
        }

        remove.setOnClickListener {
            if(Integer.parseInt(qty.text.toString()) > 0)
            {
                var hasil = Integer.parseInt(qty.text.toString()) - 1;
                qty.text = hasil.toString()
            }
        }

        check.setOnClickListener {
            if (check.isChecked) {
                var dataco = mutableListOf<String>().apply {
                    add(0, product.id)
                    add(1, product.title)
                    add(2, product.price)
                    add(3, product.category)
                    add(4, product.desc)
                    add(5, product.image)
                    add(6, product.date)
                    add(7, product.qty)
                }
                dataCheckOut.add(0, dataco)
                countData += data[position].price.toDouble() * product.qty.toDouble()
            } else {
                dataCheckOut.removeAt(position)
                countData -= data[position].price.toDouble() * product.qty.toDouble()
            }
            total.text = countData.toString()
        }



        return view
    }

}