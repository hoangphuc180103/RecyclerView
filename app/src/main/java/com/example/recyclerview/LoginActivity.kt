package com.example.recyclerview


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Khởi tạo SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)


        // Ánh xạ view
        val edtEmail = findViewById<EditText>(R.id.edtLoginEmail)
        val edtPassword = findViewById<EditText>(R.id.edtLoginPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnRegister = findViewById<Button>(R.id.btnRegister)



        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Đăng nhập admin
            if (email == "admin" && password == "admin") {
                val editor = sharedPreferences.edit()
                editor.putString("role", "admin")
                editor.putString("email", email)
                editor.putString("password", password)
                editor.apply()

                startActivity(Intent(this, HomeActivity::class.java))
                finish()
                return@setOnClickListener
            }

            // Kiểm tra định dạng email hợp lệ
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


        }
    }
}
