package com.cse22201576.carerapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FamilySignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val role = intent.getStringExtra("role") ?: "FAMILY"

        val etName = findViewById<EditText>(R.id.etFamilyName)
        val etPhone = findViewById<EditText>(R.id.etFamilyPhone)
        val etRelation = findViewById<EditText>(R.id.etRelation)
        val etAddress = findViewById<EditText>(R.id.etAddress)
        val btnCreate = findViewById<Button>(R.id.btnCreateFamily)

        supportActionBar?.title = "Family Registration"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val relation = etRelation.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || relation.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // create Firebase account
            auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener { result ->
                    val uid = result.user?.uid ?: return@addOnSuccessListener

                    val userData = hashMapOf(
                        "uid" to uid,
                        "full_name" to name,
                        "email" to email,
                        "phone" to phone,
                        "role" to role,
                        "relation" to relation,
                        "address" to address,
                        "created_at" to System.currentTimeMillis()
                    )

                    db.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                            goToDashboard(role)
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun goToDashboard(role: String) {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("role", role)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}