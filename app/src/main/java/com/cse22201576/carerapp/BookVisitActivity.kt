package com.cse22201576.carerapp

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class BookVisitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_visit)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Book a Visit"

        val carerName = intent.getStringExtra("carer_name") ?: ""
        findViewById<TextView>(R.id.tvBookingCarer).text = "Booking with: $carerName"
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) { finish(); return true }
        return super.onOptionsItemSelected(item)
    }
}