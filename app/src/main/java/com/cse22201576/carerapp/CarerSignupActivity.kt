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
    private var selectedCarerType = "registered"

    private val hospitals = listOf(
        "Square Hospital",
        "Labaid Hospital",
        "United Hospital",
        "Apollo Hospital",
        "Ibn Sina Hospital",
        "Other"
    )

    private val pickPhoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedPhotoUri = it

            findViewById<ImageView>(R.id.ivCarerPhoto)
                .setImageURI(it)

            findViewById<TextView>(R.id.tvPhotoLabel)
                .text = "Photo selected ✓"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carer_signup)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()

        supportActionBar?.title = "Carer Registration"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val etName = findViewById<EditText>(R.id.etCarerName)
        val etEmail = findViewById<EditText>(R.id.etCarerEmail)
        val etPassword = findViewById<EditText>(R.id.etCarerPassword)
        val etPhone = findViewById<EditText>(R.id.etCarerPhone)

        val etBio = findViewById<EditText>(R.id.etBio)
        val etCredentials = findViewById<EditText>(R.id.etCredentials)
        val etExperience = findViewById<EditText>(R.id.etExperience)
        val etRate = findViewById<EditText>(R.id.etRate)

        val etHospitalOther =
            findViewById<EditText>(R.id.etHospitalOther)

        val spinnerType =
            findViewById<Spinner>(R.id.spinnerCarerType)

        val spinnerHospital =
            findViewById<Spinner>(R.id.spinnerHospital)

        val layoutHospital =
            findViewById<LinearLayout>(R.id.layoutHospital)

        val btnPhoto =
            findViewById<Button>(R.id.btnSelectPhoto)

        val btnCreate =
            findViewById<Button>(R.id.btnCreateCarer)


        val typeOptions = listOf(
            "Registered Nurse",
            "Specialized Nurse"
        )

        spinnerType.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            typeOptions
        )

        spinnerType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedCarerType =
                        if (position == 0) {
                            "registered"
                        } else {
                            "specialized"
                        }

                    layoutHospital.visibility =
                        if (position == 1) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>
                ) {
                }
            }

        spinnerHospital.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            hospitals
        )

        spinnerHospital.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    etHospitalOther.visibility =
                        if (hospitals[position] == "Other") {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                }

                override fun onNothingSelected(
                    parent: AdapterView<*>
                ) {
                }
            }


        btnPhoto.setOnClickListener {
            pickPhoto.launch("image/*")
        }

        btnCreate.setOnClickListener {

            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val phone = etPhone.text.toString().trim()

            val bio = etBio.text.toString().trim()
            val credentials =
                etCredentials.text.toString().trim()

            val experience =
                etExperience.text.toString().trim()

            val rate =
                etRate.text.toString().trim()

            if (
                name.isEmpty() ||
                email.isEmpty() ||
                password.isEmpty() ||
                phone.isEmpty() ||
                credentials.isEmpty() ||
                experience.isEmpty() ||
                rate.isEmpty()
            ) {
                Toast.makeText(
                    this,
                    "Please fill all required fields",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(
                    this,
                    "Password must be at least 6 characters",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val hospitalName =
                if (selectedCarerType == "specialized") {

                    val selectedHospital =
                        spinnerHospital.selectedItem.toString()

                    if (selectedHospital == "Other") {
                        etHospitalOther.text
                            .toString()
                            .trim()
                    } else {
                        selectedHospital
                    }

                } else {
                    ""
                }

            createCarerAccount(
                email = email,
                password = password,
                name = name,
                phone = phone,
                bio = bio,
                credentials = credentials,
                experience = experience,
                rate = rate,
                hospital = hospitalName
            )
        }
    }

    private fun createCarerAccount(
        email: String,
        password: String,
        name: String,
        phone: String,
        bio: String,
        credentials: String,
        experience: String,
        rate: String,
        hospital: String
    ) {

        auth.createUserWithEmailAndPassword(
            email,
            password
        )
            .addOnSuccessListener { result ->

                val uid = result.user?.uid
                    ?: return@addOnSuccessListener

                if (selectedPhotoUri != null) {

                    uploadPhotoAndSave(
                        uid = uid,
                        email = email,
                        name = name,
                        phone = phone,
                        bio = bio,
                        credentials = credentials,
                        experience = experience,
                        rate = rate,
                        hospital = hospital
                    )

                } else {

                    saveCarerData(
                        uid = uid,
                        email = email,
                        name = name,
                        phone = phone,
                        bio = bio,
                        credentials = credentials,
                        experience = experience,
                        rate = rate,
                        hospital = hospital,
                        photoUrl = ""
                    )
                }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Signup failed: ${it.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun uploadPhotoAndSave(
        uid: String,
        email: String,
        name: String,
        phone: String,
        bio: String,
        credentials: String,
        experience: String,
        rate: String,
        hospital: String
    ) {

        val ref = storage.reference
            .child(
                "carer_photos/${UUID.randomUUID()}.jpg"
            )

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {

                ref.downloadUrl
                    .addOnSuccessListener { uri ->

                        saveCarerData(
                            uid = uid,
                            email = email,
                            name = name,
                            phone = phone,
                            bio = bio,
                            credentials = credentials,
                            experience = experience,
                            rate = rate,
                            hospital = hospital,
                            photoUrl = uri.toString()
                        )
                    }
            }
            .addOnFailureListener {

                saveCarerData(
                    uid = uid,
                    email = email,
                    name = name,
                    phone = phone,
                    bio = bio,
                    credentials = credentials,
                    experience = experience,
                    rate = rate,
                    hospital = hospital,
                    photoUrl = ""
                )
            }
    }

    private fun saveCarerData(
        uid: String,
        email: String,
        name: String,
        phone: String,
        bio: String,
        credentials: String,
        experience: String,
        rate: String,
        hospital: String,
        photoUrl: String
    ) {

        val userData = hashMapOf(
            "uid" to uid,
            "full_name" to name,
            "email" to email,
            "phone" to phone,
            "role" to "CARER",
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

        db.collection("users")
            .document(uid)
            .set(userData)
            .addOnSuccessListener {

                // Also save in carer_profiles
                db.collection("carer_profiles")
                    .document(uid)
                    .set(userData)
                    .addOnSuccessListener {

                        Toast.makeText(
                            this,
                            "Account created!",
                            Toast.LENGTH_SHORT
                        ).show()

                        goToDashboard()
                    }
                    .addOnFailureListener {

                        Toast.makeText(
                            this,
                            "Failed to save carer profile",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Failed to save user data",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun goToDashboard() {

        val intent =
            Intent(this, DashboardActivity::class.java)

        intent.putExtra("role", "CARER")

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}