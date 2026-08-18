package com.cse22201576.carerapp

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class HomeFragment : Fragment() {

    private var role = "FAMILY"
    private lateinit var adapter: CarerAdapter
    private var allCarers = listOf<CarerProfile>()
    private var currentFilter = "All"

    companion object {
        fun newInstance(role: String): HomeFragment {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("role", role)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        role = arguments?.getString("role") ?: "FAMILY"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val recycler = view.findViewById<RecyclerView>(R.id.recyclerCarers)
        val layoutCarerHome = view.findViewById<View>(R.id.layoutCarerHome)
        val etSearch = view.findViewById<EditText>(R.id.etSearch)
        val btnAll = view.findViewById<Button>(R.id.btnAll)
        val btnRegistered = view.findViewById<Button>(R.id.btnRegistered)
        val btnSpecialized = view.findViewById<Button>(R.id.btnSpecialized)

        if (role == "CARER") {
            // carer sees their own dashboard
            recycler.visibility = View.GONE
            etSearch.visibility = View.GONE
            btnAll.visibility = View.GONE
            btnRegistered.visibility = View.GONE
            btnSpecialized.visibility = View.GONE
            layoutCarerHome.visibility = View.VISIBLE
            return view
        }

        // FAMILY view
        adapter = CarerAdapter(emptyList()) { carer ->
            val intent = Intent(requireContext(), CarerDetailActivity::class.java)
            intent.putExtra("uid", carer.uid)
            intent.putExtra("name", carer.fullName)
            intent.putExtra("type", carer.carerType)
            intent.putExtra("hospital", carer.hospital)
            intent.putExtra("bio", carer.bio)
            intent.putExtra("credentials", carer.credentials)
            intent.putExtra("experience", carer.experienceYears)
            intent.putExtra("rate", carer.hourlyRate)
            intent.putExtra("rating", carer.ratingAvg)
            startActivity(intent)
        }

        recycler.layoutManager = LinearLayoutManager(requireContext())
        recycler.adapter = adapter

        // load carers from Firestore
        loadCarers()

        // search
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterCarers(s.toString(), currentFilter)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // filter buttons
        fun setFilter(filter: String) {
            currentFilter = filter
            filterCarers(etSearch.text.toString(), filter)
            val active = "@color/primary"
            btnAll.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(if (filter == "All") "#4A90A4" else "#EEF6F9"))
            btnRegistered.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(if (filter == "Registered") "#4A90A4" else "#EEF6F9"))
            btnSpecialized.backgroundTintList = android.content.res.ColorStateList.valueOf(
                android.graphics.Color.parseColor(if (filter == "Specialized") "#4A90A4" else "#EEF6F9"))

            btnAll.setTextColor(android.graphics.Color.parseColor(if (filter == "All") "#FFFFFF" else "#1A2B35"))
            btnRegistered.setTextColor(android.graphics.Color.parseColor(if (filter == "Registered") "#FFFFFF" else "#1A2B35"))
            btnSpecialized.setTextColor(android.graphics.Color.parseColor(if (filter == "Specialized") "#FFFFFF" else "#1A2B35"))
        }

        btnAll.setOnClickListener { setFilter("All") }
        btnRegistered.setOnClickListener { setFilter("Registered") }
        btnSpecialized.setOnClickListener { setFilter("Specialized") }

        return view
    }

    private fun loadCarers() {
        FirebaseFirestore.getInstance()
            .collection("carer_profiles")
            .get()
            .addOnSuccessListener { documents ->
                allCarers = documents.map { doc ->
                    CarerProfile(
                        uid = doc.id,
                        fullName = doc.getString("full_name") ?: "",
                        carerType = doc.getString("carer_type") ?: "",
                        hospital = doc.getString("hospital") ?: "",
                        bio = doc.getString("bio") ?: "",
                        credentials = doc.getString("credentials") ?: "",
                        experienceYears = doc.getString("experience_years") ?: "",
                        hourlyRate = doc.getString("hourly_rate") ?: "",
                        ratingAvg = doc.getDouble("rating_avg") ?: 0.0,
                        isVerified = doc.getBoolean("is_verified") ?: false,
                        photoUrl = doc.getString("photo_url") ?: ""
                    )
                }
                adapter.updateList(allCarers)
            }
            .addOnFailureListener {
                android.widget.Toast.makeText(
                    requireContext(),
                    "Failed to load carers: ${it.message}",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun filterCarers(query: String, filter: String) {
        var filtered = allCarers
        if (filter != "All") {
            filtered = filtered.filter { it.carerType == filter }
        }
        if (query.isNotEmpty()) {
            filtered = filtered.filter {
                it.fullName.contains(query, ignoreCase = true) ||
                        it.hospital.contains(query, ignoreCase = true)
            }
        }
        adapter.updateList(filtered)
    }
}