package com.example.recyclerview

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("productName")
        val imageUri = intent.getStringExtra("productImage")


        val imgDetail = findViewById<ImageView>(R.id.imgDetail)
        val txtDetailName = findViewById<TextView>(R.id.txtDetailName)
        val btnAddToCart = findViewById<android.widget.Button>(R.id.btnAddToCart)

        txtDetailName.text = name
        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(Uri.parse(imageUri))  // Convert String to URI
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(imgDetail)
        } else {
            imgDetail.setImageResource(R.drawable.ic_launcher_foreground)
        }


        createNotificationChannel()
        btnAddToCart.setOnClickListener {
            showCartNotification()
            android.widget.Toast.makeText(this, "Added to cart!", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Cart Notification"
            val descriptionText = "Notifies if cart has items"
            val importance = android.app.NotificationManager.IMPORTANCE_DEFAULT
            val channel = android.app.NotificationChannel("cart_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: android.app.NotificationManager =
                getSystemService(android.content.Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showCartNotification() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                android.widget.Toast.makeText(this, "Please grant notification permission in app settings.", android.widget.Toast.LENGTH_SHORT).show()
                return
            }
        }
        val intent = android.content.Intent(this, CartActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: android.app.PendingIntent = android.app.PendingIntent.getActivity(
            this, 0, intent, android.app.PendingIntent.FLAG_IMMUTABLE
        )
        val builder = androidx.core.app.NotificationCompat.Builder(this, "cart_channel")
            .setSmallIcon(R.drawable.ic_cart)
            .setContentTitle("Cart Reminder")
            .setContentText("You have items in your cart.")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        val notificationId = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
        with(androidx.core.app.NotificationManagerCompat.from(this)) {
            notify(notificationId, builder.build())
        }
    }
}
