package com.example.memoapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var etUsername: EditText
    lateinit var etPassword: EditText
    lateinit var cbRemember: CheckBox
    lateinit var btnLogin: Button
    lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化视图
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        cbRemember = findViewById(R.id.cbRemember)
        btnLogin = findViewById(R.id.btnLogin)

        // 初始化 SharedPreferences
        sharedPref = getSharedPreferences("user_prefs", MODE_PRIVATE)

        // 如果已保存用户状态，则直接跳转
        val isRemembered = sharedPref.getBoolean("isRemembered", false)
        if (isRemembered) {
            goToMemoList()
        }

        // 登录按钮点击事件
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString()
            val password = etPassword.text.toString()

            if (username == "admin" && password == "123456") {
                // 保存登录状态
                val editor = sharedPref.edit()
                editor.putBoolean("isRemembered", cbRemember.isChecked)
                editor.apply()

                goToMemoList()
            } else {
                Toast.makeText(this, "用户名或密码错误", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToMemoList() {
        val intent = Intent(this, MemoListActivity::class.java)
        startActivity(intent)
        finish()
    }
}
