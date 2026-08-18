package com.cse22201576.carerapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class CarerSignupActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedPhotoUri: Uri? = null
    private var selectedCarerType = "Registered"

    private val hospitals = listOf(
        "Square Hospital",
        "Labaid Hospital",
        "United Hospital",
        "Apollo Hospital",
        "Ibn Sina Hospital",
        "Other"
    )

    // photo picker
    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPhotoUri = it
            findViewById<ImageView>(R.id.ivCarerPhoto).setImageURI(it)
            findViewById<TextView>(R.id.tvPhotoLabel).text = "Photo selected ✓"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carer_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val role = intent.getStringExtra("role") ?: "CARER"

        supportActionBar?.title = "Carer Registration"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val etName = findViewById<EditText>(R.id.etCarerName)
        val etPhone = findViewById<EditText>(R.id.etCarerPhone)
        val etBio = findViewById<EditText>(R.id.etBio)
        val etCredentials = findViewById<EditText>(R.id.etCredentials)
        val etExperience = findViewById<EditText>(R.id.etExperience)
        val etRate = findViewById<EditText>(R.id.etRate)
        val etHospitalOther = findViewById<EditText>(R.id.etHospitalOther)
        val spinnerType = findViewById<Spinner>(R.id.spinnerCarerType)
        val spinnerHospital = findViewById<Spinner>(R.id.spinnerHospital)
        val layoutHospital = findViewById<LinearLayout>(R.id.layoutHospital)
        val btnPhoto = findViewById<Button>(R.id.btnSelectPhoto)
        val btnCreate = findViewById<Button>(R.id.btnCreateCarer)

        // carer type spinner
        val typeOptions = listOf("Registered Nurse", "Specialized Nurse")
        spinnerType.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, typeOptions)

        spinnerType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                selectedCarerType = if (pos == 0) "Registered" else "Specialized"
                // show hospital section only for specialized nurses
                layoutHospital.visibility = if (pos == 1) View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // hospital spinner
        spinnerHospital.adapter = ArrayAdapter(this,
            android.R.layout.simple_spinner_dropdown_item, hospitals)

        spinnerHospital.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                // show text input if "Other" selected
                etHospitalOther.visibility = if (hospitals[pos] == "Other") View.VISIBLE else View.GONE
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        btnPhoto.setOnClickListener {
            pickPhoto.launch("image/*")
        }

        btnCreate.setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val bio = etBio.text.toString().trim()
            val credentials = etCredentials.text.toString().trim()
            val experience = etExperience.text.toString().trim()
            val rate = etRate.text.toString().trim()

            if (name.isEmpty() || phone.isEmpty() || credentials.isEmpty()) {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val hospitalName = if (selectedCarerType == "Specialized") {
                val selected = spinnerHospital.selectedItem.toString()
                if (selected == "Other") etHospitalOther.text.toString().trim()
                else selected
            } else ""

            createCarerAccount(email, password, role, name, phone, bio,
                credentials, experience, rate, hospitalName)
        }
    }

    private fun createCarerAccount(
        email: String, password: String, role: String,
        name: String, phone: String, bio: String,
        credentials: String, experience: String,
        rate: String, hospital: String
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                if (selectedPhotoUri != null) {
                    // upload photo first then save data
                    uploadPhotoAndSave(uid, email, role, name, phone, bio,
                        credentials, experience, rate, hospital)
                } else {
                    saveCarerData(uid, email, role, name, phone, bio,
                        credentials, experience, rate, hospital, "")
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Signup failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadPhotoAndSave(
        uid: String, email: String, role: String,
        name: String, phone: String, bio: String,
        credentials: String, experience: String,
        rate: String, hospital: String
    ) {
        val ref = storage.reference.child("carer_photos/${UUID.randomUUID()}.jpg")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                ref.downloadUrl.addOnSuccessListener { uri ->
                    saveCarerData(uid, email, role, name, phone, bio,
                        credentials, experience, rate, hospital, uri.toString())
                }
            }
            .addOnFailureListener {
                // save without photo if upload fails
                saveCarerData(uid, email, role, name, phone, bio,
                    credentials, experience, rate, hospital, "")
            }
    }

    private fun saveCarerData(
        uid: String, email: String, role: String,
        name: String, phone: String, bio: String,
        credentials: String, experience: String,
        rate: String, hospital: String, photoUrl: String
    ) {
        val userData = hashMapOf(
            "uid" to uid,
            "full_name" to name,
            "email" to email,
            "phone" to phone,
            "role" to role,
            "carer_type" to selectedCarerType,
            "hospital" to hospital,
            "bio" to bio,
            "credentials" to credentials,
            "experience_years" to experience,
            "hourly_rate" to rate,
            "photo_url" to photoUrl,
            "is_verified" to false,
            "rating_avg" to 0.0,
            "created_at" to System.currentTimeMillis()
        )

        db.collection("users").document(uid)
            .set(userData)
            .addOnSuccessListener {
                // also save to carer_profiles collection
                db.collection("carer_profiles").document(uid)
                    .set(userData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                        goToDashboard(role)
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save data", Toast.LENGTH_SHORT).show()
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