package com.example.recyclerview

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val productList = listOf(
        Product("Pharmacy", R.drawable.pharmacy),
        Product("Registry", R.drawable.registry),
        Product("Cartwheel", R.drawable.cartwheel),
        Product("Clothing", R.drawable.clothing),
        Product("Shoes", R.drawable.shoes),
        Product("Accessories", R.drawable.accessories),
        Product("Baby", R.drawable.baby),
        Product("Home", R.drawable.home),
        Product("Patio & Garden", R.drawable.patio)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val adapter = ProductAdapter(productList) { product ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("productName", product.name)
            intent.putExtra("productImage", product.imageResId)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }
}
