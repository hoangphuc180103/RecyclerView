package com.example.recyclerview

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("productName")
        val image = intent.getIntExtra("productImage", R.drawable.ic_launcher_foreground)

        val imgDetail = findViewById<ImageView>(R.id.imgDetail)
        val txtDetailName = findViewById<TextView>(R.id.txtDetailName)

        txtDetailName.text = name
        imgDetail.setImageResource(image)
    }
}
