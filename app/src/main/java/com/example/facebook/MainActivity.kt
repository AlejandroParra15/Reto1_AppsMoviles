package com.example.facebook

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var txtEmail:EditText
    private lateinit var txtPassword:EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var listPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("SHARED_PREF", Context.MODE_PRIVATE)
        listPreferences = getSharedPreferences("LIST_PREF", Context.MODE_PRIVATE)
        val n = sharedPreferences.getString("EMAIL","")
        val p = sharedPreferences.getString("PASSWORD","")
        val gson = Gson()
        val json = listPreferences.getString("LIST", null)
        val arrayTutorialType = object : TypeToken<ArrayList<Post>>() {}.type
        if(json == null){
            PostFragment.postsList = arrayListOf<Post>()
            val editor : SharedPreferences.Editor = listPreferences.edit()
            val gson = Gson()
            val json : String = gson.toJson(PostFragment.postsList)
            editor.putString("LIST",json)
            editor.apply()
        }else{
            PostFragment.postsList = gson.fromJson(json, arrayTutorialType)
        }
        if(PostFragment.postsList.isEmpty() || PostFragment.postsList == null){
            PostFragment.postsList = arrayListOf<Post>()
        }
        if(n != "" && p != ""){
            startActivity(Intent(this,Principal::class.java))
            finish()
        }

        txtEmail = findViewById(R.id.email)
        txtPassword = findViewById(R.id.password)


    }

    fun logIN(view: View){
        val email:String =txtEmail.text.toString()
        val password:String =txtPassword.text.toString()
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        if(email.isNotEmpty()){
            when (email) {
                "alfa@gmail.com" -> if(password=="aplicacionesmoviles"){
                    editor.putString("EMAIL",email)
                    editor.putString("PASSWORD",password)
                    editor.apply()
                    startActivity(Intent(this,Principal::class.java))
                    finish()
                } else Toast.makeText(this, "Correo/Contraseña incorrecta", Toast.LENGTH_LONG).show();
                "beta@gmail.com" -> if(password=="aplicacionesmoviles"){
                    editor.putString("EMAIL",email)
                    editor.putString("PASSWORD",password)
                    editor.apply()
                    startActivity(Intent(this,Principal::class.java))
                    finish()
                } else Toast.makeText(this, "Correo/Contraseña incorrecta", Toast.LENGTH_LONG).show();
                else -> Toast.makeText(this, "Correo/Contraseña incorrecta", Toast.LENGTH_LONG).show();
            }
        }else Toast.makeText(this, "Debe completar el campo email", Toast.LENGTH_LONG).show();

    }
}