package com.cse22201576.carerapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnFamily = findViewById<Button>(R.id.btnFamily)
        val btnCarer = findViewById<Button>(R.id.btnCarer)

        // when family button tapped — go to login/signup as FAMILY
        btnFamily.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("role", "FAMILY")
            startActivity(intent)
        }

        // when carer button tapped — go to login/signup as CARER
        btnCarer.setOnClickListener {
            val intent = Intent(this, AuthActivity::class.java)
            intent.putExtra("role", "CARER")
            startActivity(intent)
        }
    }
}