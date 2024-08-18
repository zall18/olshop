package com.example.olshop

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var session: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var email: EditText = findViewById(R.id.email_input)
        var password: EditText = findViewById(R.id.pass_input)
        var login: AppCompatButton = findViewById(R.id.login_button)
        var  connection = connection()

        session = getSharedPreferences("session", Context.MODE_PRIVATE)
        var editor = session.edit()

        login.setOnClickListener {
            if(email.text.isEmpty()){
                email.setError("This field is required")
            }else if(password.text.isEmpty()){
                password.setError("This field is required")
            }else{

                var jsonObject = JSONObject().apply {
                    put("username", email.text.toString())
                    put("password", password.text.toString())
                }

                lifecycleScope.launch {
                    var result = postRequest(connection.Connection + "auth/login", jsonObject, null)

                    result.fold(
                        onSuccess = { response -> var jsonObject2 = JSONObject(response)

                            if(!jsonObject2.getString("token").isNullOrEmpty())
                            {
                                editor.putString("token", jsonObject2.getString("token"))
                                editor.commit()

                                startActivity(Intent(applicationContext, bottomNav::class.java))
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
        }

        var daftar1: TextView = findViewById(R.id.daftar1)
        daftar1.setOnClickListener{
            startActivity(Intent(applicationContext, daftar::class.java))
        }

        var daftar2: TextView = findViewById(R.id.daftar2)
        daftar2.setOnClickListener{
            startActivity(Intent(applicationContext, daftar::class.java))
        }

        var google: LinearLayout = findViewById(R.id.google_login)
        google.setOnClickListener {
            Toast.makeText(applicationContext, "This  feature avaible  soon", Toast.LENGTH_SHORT).show()
        }

        var facebook: LinearLayout = findViewById(R.id.facebook_login)
        facebook.setOnClickListener {
            Toast.makeText(applicationContext, "This  feature avaible  soon", Toast.LENGTH_SHORT).show()
        }
    }
}