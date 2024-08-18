package com.example.olshop

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ProductAdapter(var context: Context, var data: MutableList<ProductModel>): BaseAdapter(), Filterable {

    var inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    var imageLoad = MainScope()
    var filterData: MutableList<ProductModel> = data

    override fun getCount(): Int {
        return filterData.size
    }

    override fun getItem(position: Int): Any {
        return filterData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView ?: inflater.inflate(R.layout.productitem, null, false)

        var image = view.findViewById<ShapeableImageView>(R.id.image_product)
        var name = view.findViewById<TextView>(R.id.name_product)
        var price = view.findViewById<TextView>(R.id.price_product)
        var category  = view.findViewById<TextView>(R.id.category_product)

        var product = getItem(position) as ProductModel

        name.text = product.title
        price.text = "Rp. " + product.price.toString()
        category.text = product.category
        imageLoad.launch {
            var bitmap = getImageFromUrl(product.image)
            image.setImageBitmap(bitmap)
        }

        var produk = view.findViewById<LinearLayout>(R.id.product)
        produk.setOnClickListener {
            var intent = Intent(context, detailProduk::class.java)
            intent.putExtra("idproduct", product.id)
            context.startActivity(intent)
        }

        return view
    }

    override fun getFilter(): Filter {
        return object : Filter()
        {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filtering = mutableListOf<ProductModel>()

                if(constraint == null)
                {
                    filtering.addAll(data)
                }else{
                    var query = constraint.toString().lowercase().trim()

                    for (item in data)
                    {
                        if(item.title.toString().lowercase().trim().contains(query))
                        {
                            filtering.add(ProductModel(item.id, item.title, item.price, item.category, item.desc, item.image))
                        }
                    }

                }

                var result = FilterResults()
                result.values = filtering
                return result
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filterData = results?.values as ArrayList<ProductModel>
                notifyDataSetChanged()
            }

        }
    }
}