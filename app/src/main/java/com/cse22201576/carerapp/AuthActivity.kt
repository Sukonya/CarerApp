package com.cse22201576.carerapp

import android.content.Intent
import android.os.Bundle
import android.view.View
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
        role = intent.getStringExtra("role") ?: "FAMILY"

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val btnLogIn = findViewById<Button>(R.id.btnLogIn)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        val tvRole = findViewById<TextView>(R.id.tvRole)

        tvRole.text = "Role: $role"

        btnSignUp.setOnClickListener {

            isSignUp = true

            etName.visibility = View.VISIBLE
            etPhone.visibility = View.VISIBLE

            btnSubmit.text = "SIGN UP"
        }

        btnLogIn.setOnClickListener {

            isSignUp = false

            etName.visibility = View.GONE
            etPhone.visibility = View.GONE

            btnSubmit.text = "LOG IN"
        }

        btnSubmit.setOnClickListener {

            if (isSignUp) {
                if (role == "CARER") {
                    val intent = Intent(
                        this,
                        CarerSignupActivity::class.java
                    )

                    startActivity(intent)

                } else {
                    val email =
                        etEmail.text.toString().trim()

                    val password =
                        etPassword.text.toString().trim()

                    if (email.isEmpty() || password.isEmpty()) {

                        Toast.makeText(
                            this,
                            "Email and password are required",
                            Toast.LENGTH_SHORT
                        ).show()

                        return@setOnClickListener
                    }

                    val intent = Intent(
                        this,
                        FamilySignupActivity::class.java
                    )

                    intent.putExtra(
                        "email",
                        email
                    )

                    intent.putExtra(
                        "password",
                        password
                    )

                    intent.putExtra(
                        "role",
                        "FAMILY"
                    )

                    startActivity(intent)
                }

            } else {
                val email =
                    etEmail.text.toString().trim()

                val password =
                    etPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {

                    Toast.makeText(
                        this,
                        "Email and password are required",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@setOnClickListener
                }

                logIn(email, password)
            }
        }
    }

    private fun logIn(
        email: String,
        password: String
    ) {

        auth.signInWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener { result ->

                val uid =
                    result.user?.uid
                        ?: return@addOnSuccessListener

                db.collection("users")
                    .document(uid)
                    .get()
                    .addOnSuccessListener { doc ->

                        val userRole =
                            doc.getString("role")
                                ?: "FAMILY"

                        Toast.makeText(
                            this,
                            "Welcome back!",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToDashboard(userRole)
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Failed to fetch user data",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Login failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun goToDashboard(
        userRole: String
    ) {

        val intent = Intent(
            this,
            DashboardActivity::class.java
        )

        intent.putExtra(
            "role",
            userRole
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }
}