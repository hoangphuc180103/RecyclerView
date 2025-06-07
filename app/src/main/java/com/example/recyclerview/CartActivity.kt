package com.example.recyclerview

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val txtCartInfo = findViewById<TextView>(R.id.txtCartInfo)
        txtCartInfo.text = "You have items in your cart!"
    }
}
