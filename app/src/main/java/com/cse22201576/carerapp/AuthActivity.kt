package com.cse22201576.carerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private var role = "FAMILY"
    private var isSignUp = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // get role passed from MainActivity
        role = intent.getStringExtra("role") ?: "FAMILY"

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val btnLogIn = findViewById<Button>(R.id.btnLogIn)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val tvRole = findViewById<TextView>(R.id.tvRole)

        // show which role was selected
        tvRole.text = "Role: $role"

        // toggle between signup and login
        btnSignUp.setOnClickListener {
            isSignUp = true
            etName.visibility = android.view.View.VISIBLE
            etPhone.visibility = android.view.View.VISIBLE
            btnSubmit.text = "CREATE ACCOUNT"
        }

        btnLogIn.setOnClickListener {
            isSignUp = false
            etName.visibility = android.view.View.GONE
            etPhone.visibility = android.view.View.GONE
            btnSubmit.text = "LOG IN"
        }

        btnSubmit.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isSignUp) {
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                signUp(email, password, name, phone)
            } else {
                logIn(email, password)
            }
        }
    }

    private fun signUp(email: String, password: String, name: String, phone: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                // save user data to Firestore
                val userData = hashMapOf(
                    "uid" to uid,
                    "full_name" to name,
                    "email" to email,
                    "phone" to phone,
                    "role" to role,
                    "created_at" to System.currentTimeMillis()
                )

                db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                        goToDashboard()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to save user data", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign up failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun logIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                goToDashboard()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToDashboard() {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("role", role)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}