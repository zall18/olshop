package com.example.olshop

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Layout.Alignment
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintSet.Layout
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class Invoice : AppCompatActivity() {

    lateinit var file : File
    lateinit var f : File
    var tipe = "PDF"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_invoice)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var extra = intent.getStringExtra("data")
        val linear = findViewById<LinearLayout>(R.id.linear)
        var content: LinearLayout = findViewById(R.id.content)
        var total = 0.0

        println(extra)
        var jsonArray = JSONArray(extra)
        for (i in 0 until jsonArray.length())
        {
            var jsonObject2 = jsonArray.getJSONObject(i)
            println(jsonObject2.getString("title"))
            val linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.HORIZONTAL

            var textView: TextView = TextView(this)
            val params: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params.weight = 1.0f
            textView.setText(jsonObject2.getString("title"))
            textView.setTextColor(Color.BLACK)
            textView.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView.layoutParams = params
            linearLayout.addView(textView)

            var textView2: TextView = TextView(this)
            val params2: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params2.weight = 1.0f
            textView2.setText(jsonObject2.getString("price"))
            textView2.setTextColor(Color.BLACK)
            textView2.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView2.layoutParams = params
            linearLayout.addView(textView2)

            var textView3: TextView = TextView(this)
            val params3: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params3.weight = 1.0f
            textView3.setText(jsonObject2.getString("quantity"))
            textView3.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView3.setTextColor(Color.BLACK)
            textView3.layoutParams = params
            linearLayout.addView(textView3)

            var textView4: TextView = TextView(this)
            val params4: LinearLayout.LayoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT)
            params4.weight = 1.0f
            textView4.setText((jsonObject2.getString("price").toDouble() * jsonObject2.getString("quantity").toDouble()).toString())
            textView4.setTextColor(Color.BLACK)
            textView4.textAlignment = View.TEXT_ALIGNMENT_CENTER
            textView4.layoutParams = params
            linearLayout.addView(textView4)
            linear.addView(linearLayout)

            var view: View = View(this)
            val params5: LinearLayout.LayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 1)
            view.layoutParams = params5
            view.setBackgroundColor(Color.BLACK)
            linear.addView(view)

            total += jsonObject2.getString("price").toDouble() * jsonObject2.getString("quantity").toDouble()

        }

        var tb: TextView = findViewById(R.id.tb_invoice)
        tb.text = "Rp. " + total.toString()
        var save: AppCompatButton = findViewById(R.id.save_invoice)
        save.setOnClickListener {
            var dialog = BottomSheetDialog(this)
            var view = layoutInflater.inflate(R.layout.option_save, null)

            var bitmap = createBitmap(content.width, content.height, Bitmap.Config.ARGB_8888)
            var canvas = Canvas(bitmap)
            content.draw(canvas)

            var image: AppCompatButton = view.findViewById(R.id.image)
            var pdf: AppCompatButton = view.findViewById(R.id.pdf)

            image.setOnClickListener {
                try {
                    if(android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        file = File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "olshop")
                        if(!file.exists())
                        {
                            file.mkdir()
                        }

                        f = File(file.absoluteFile.toString() + "/olshop.png")
                        var fileOutputStream = FileOutputStream(f)
                        bitmap.compress(Bitmap.CompressFormat.PNG, 10, fileOutputStream)
                        tipe = "PNG"
                        Toast.makeText(applicationContext, "success to save", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }

            pdf.setOnClickListener {
                var pdfDocument: PdfDocument = PdfDocument()
                var pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()
                var page = pdfDocument.startPage(pageInfo)

                var canvas2 = page.canvas
                canvas2.drawBitmap(bitmap, 0f, 0f, null)
                pdfDocument.finishPage(page)
                try {
                    if(android.os.Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    {
                        file = File(android.os.Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "olshop")
                        if(!file.exists())
                        {
                            file.mkdir()
                        }

                        f = File(file.absoluteFile.toString() + "/olshop.pdf")
                        var fileOutputStream = FileOutputStream(f)
                        pdfDocument.writeTo(fileOutputStream)
                        tipe = "PDP"
                        Toast.makeText(applicationContext, "success to save", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                }catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            dialog.setContentView(view)
            dialog.show()
        }

        var share: AppCompatButton = findViewById(R.id.share_invoice)
        share.setOnClickListener {
            if(tipe == "PNG")
            {
                var intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(f.toString()))
                intent.setType("image/png")
                startActivity(Intent.createChooser(intent, "Share image via.."))
            }else{
                var uri = FileProvider.getUriForFile(applicationContext, "olshop", f.absoluteFile)
                var intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uri.toString()))
                intent.setType("application/pdf")
                startActivity(Intent.createChooser(intent, "Share file via.."))
            }
        }

    }
}