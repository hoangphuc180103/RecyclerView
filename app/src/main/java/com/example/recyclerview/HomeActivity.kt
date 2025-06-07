package com.example.recyclerview

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    private val NOTIFICATION_ID = 101

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

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 3)

        val adapter = ProductAdapter(productList) { product ->
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("productName", product.name)
            intent.putExtra("productImage", product.imageResId)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        // Tạo notification channel nếu cần
        createNotificationChannel()

        // Kiểm tra nếu có sản phẩm trong giỏ hàng
        val cartHasItems = true // Giả lập có sản phẩm trong giỏ
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
                Toast.makeText(this, "Cart clicked", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.action_search -> {
                Toast.makeText(this, "Search clicked", Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Cart Notification"
            val descriptionText = "Notifies if cart has items"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showCartNotification() {
        // Kiểm tra quyền POST_NOTIFICATIONS trên Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                // Không có quyền, không gửi thông báo
                Toast.makeText(this, "Please grant notification permission in app settings.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        val intent = Intent(this, CartActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_cart) // Icon giỏ hàng
            .setContentTitle("Cart Reminder")
            .setContentText("You have items in your cart.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // Sử dụng ID ngẫu nhiên dựa trên thời gian để luôn gửi lại thông báo mới
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        with(NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }
}
