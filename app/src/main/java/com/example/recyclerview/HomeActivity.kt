package com.example.recyclerview

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val CHANNEL_ID = "cart_channel"
    private lateinit var adapter: ProductAdapter
    private var productList = mutableListOf<Product>()
    private lateinit var dbHelper: ProductDatabaseHelper

    private var selectedImageUri: Uri? = null
    private val IMAGE_PICK_CODE = 1001


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        dbHelper = ProductDatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        // Toolbar và SharedPreferences
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val email = sharedPreferences.getString("email", "User")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = "Hi $email"
        setSupportActionBar(toolbar)

        // Load dữ liệu từ SQLite
        loadData()

        // Tạo notification nếu có sản phẩm trong giỏ
        createNotificationChannel()
        val cartHasItems = true
        if (cartHasItems) {
            showCartNotification()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cart -> {
                startActivity(Intent(this, CartActivity::class.java))
                true
            }
            R.id.action_search -> {
                Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_logout -> {
                getSharedPreferences("MyPrefs", MODE_PRIVATE).edit().clear().apply()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                true
            }
            R.id.action_add -> {
                showAddProductDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddProductDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_product, null)

        val etName = dialogView.findViewById<EditText>(R.id.et_product_name)
        val etDesc = dialogView.findViewById<EditText>(R.id.et_description)
        val etStatus = dialogView.findViewById<EditText>(R.id.et_status)
        val etPrice = dialogView.findViewById<EditText>(R.id.et_price)
        val etStock = dialogView.findViewById<EditText>(R.id.et_stock)
        val imgSelected = dialogView.findViewById<ImageView>(R.id.img_selected)

        imgSelected.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        AlertDialog.Builder(this)
            .setTitle("Add Product")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString()
                val description = etDesc.text.toString()
                val status = etStatus.text.toString()
                val price = etPrice.text.toString().toDoubleOrNull() ?: 0.0
                val stock = etStock.text.toString().toIntOrNull() ?: 0
                val imageUriStr = selectedImageUri?.toString() ?: ""

                if (imageUriStr.isEmpty()) {
                    Toast.makeText(this, "Please select an image", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val product = Product(0, name, imageUriStr, description, status, price, stock)
                val id = dbHelper.insertProduct(product)
                if (id != -1L) {
                    Toast.makeText(this, "Product added!", Toast.LENGTH_SHORT).show()
                    selectedImageUri = null
                    loadData()
                } else {
                    Toast.makeText(this, "Error adding product", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            selectedImageUri = data?.data
        }
    }


    private fun loadData() {
        productList = dbHelper.getAllProducts().toMutableList()
        adapter = ProductAdapter(productList) { product ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("productName", product.name)
            intent.putExtra("productImage", product.imageUrl)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Cart Notification", NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = "Notifies if cart has items"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun showCartNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Please grant notification permission.", Toast.LENGTH_SHORT).show()
            return
        }

        val intent = Intent(this, CartActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart)
            .setContentTitle("Cart Reminder")
            .setContentText("You have items in your cart.")
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(this).notify((System.currentTimeMillis() % Int.MAX_VALUE).toInt(), notification)
    }
}
