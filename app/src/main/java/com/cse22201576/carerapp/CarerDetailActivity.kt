package com.cse22201576.carerapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CarerDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carer_detail)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Carer Profile"

        val name = intent.getStringExtra("name") ?: ""
        val type = intent.getStringExtra("type") ?: ""
        val hospital = intent.getStringExtra("hospital") ?: ""
        val bio = intent.getStringExtra("bio") ?: ""
        val credentials = intent.getStringExtra("credentials") ?: ""
        val experience = intent.getStringExtra("experience") ?: ""
        val rate = intent.getStringExtra("rate") ?: ""
        val rating = intent.getDoubleExtra("rating", 0.0)
        val uid = intent.getStringExtra("uid") ?: ""

        val tvInitial = findViewById<TextView>(R.id.tvDetailInitial)
        val tvName = findViewById<TextView>(R.id.tvDetailName)
        val tvType = findViewById<TextView>(R.id.tvDetailType)
        val tvHospital = findViewById<TextView>(R.id.tvDetailHospital)
        val tvRating = findViewById<TextView>(R.id.tvDetailRating)
        val tvBio = findViewById<TextView>(R.id.tvDetailBio)
        val tvCredentials = findViewById<TextView>(R.id.tvDetailCredentials)
        val tvExperience = findViewById<TextView>(R.id.tvDetailExperience)
        val tvRate = findViewById<TextView>(R.id.tvDetailRate)
        val btnBook = findViewById<Button>(R.id.btnBookVisit)

        tvInitial.text = name.firstOrNull()?.toString() ?: "?"
        tvName.text = name
        tvType.text = type
        tvHospital.text = if (hospital.isNotEmpty()) hospital else "Independent"
        tvRating.text = "★ $rating"
        tvBio.text = if (bio.isNotEmpty()) bio else "No bio available"
        tvCredentials.text = credentials
        tvExperience.text = "$experience years of experience"
        tvRate.text = "৳$rate / hour"

        btnBook.setOnClickListener {
            val intent = Intent(this, BookVisitActivity::class.java)
            intent.putExtra("carer_uid", uid)
            intent.putExtra("carer_name", name)
            intent.putExtra("rate", rate)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}