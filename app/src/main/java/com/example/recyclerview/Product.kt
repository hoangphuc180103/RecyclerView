package com.example.recyclerview

data class Product(
    val id: Long = 0,
    val name: String,
    val imageUrl: String,
    val description: String,
    val status: String,
    val price: Double,
    val stock: Int
)