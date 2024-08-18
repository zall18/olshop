package com.example.olshop

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


class profile : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var name = view.findViewById<TextView>(R.id.name_profile)
        var email = view.findViewById<TextView>(R.id.email_profile)
        var connection = connection()

        lifecycleScope.launch {
            var result = getRequest(connection.Connection + "users/1", null)

            result.fold(
                onSuccess = {
                        response -> var jsonObject = JSONObject(response)
                    if(!jsonObject.getString("id").isNullOrEmpty())
                    {
                        name.text = jsonObject.getString("username")
                        email.text = jsonObject.getString("email")
                    }


                },
                onFailure = {
                        error -> error.printStackTrace()
                }
            )
        }

        var history = view.findViewById<AppCompatButton>(R.id.history)
        history.setOnClickListener {
            Toast.makeText(requireContext(), "this feature avaible soon", Toast.LENGTH_SHORT).show()
        }

        var toko = view.findViewById<AppCompatButton>(R.id.toko)
        toko.setOnClickListener {
            Toast.makeText(requireContext(), "this feature avaible soon", Toast.LENGTH_SHORT).show()
        }

        var test = view.findViewById<AppCompatButton>(R.id.test11)
        test.setOnClickListener {
            Toast.makeText(requireContext(), "this feature avaible soon", Toast.LENGTH_SHORT).show()
        }
    }
}