package com.example.recyclerview

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class ProductDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, "products.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE products (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                imageUrl TEXT,
                description TEXT,
                status TEXT,
                price REAL,
                stock INTEGER
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS products")
        onCreate(db)
    }

    fun insertProduct(product: Product): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", product.name)
            put("imageUrl", product.imageUrl)
            put("description", product.description)
            put("status", product.status)
            put("price", product.price)
            put("stock", product.stock)
        }
        return db.insert("products", null, values)
    }

    fun getAllProducts(): List<Product> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM products", null)
        val products = mutableListOf<Product>()

        while (cursor.moveToNext()) {
            val product = Product(
                id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("imageUrl")),
                description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                price = cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                stock = cursor.getInt(cursor.getColumnIndexOrThrow("stock"))
            )
            products.add(product)
        }
        cursor.close()
        return products
    }
}
