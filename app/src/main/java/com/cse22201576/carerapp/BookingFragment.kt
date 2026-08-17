package com.cse22201576.carerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class BookingsFragment : Fragment() {

    private var role = "FAMILY"

    companion object {
        fun newInstance(role: String): BookingsFragment {
            val fragment = BookingsFragment()
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
        val view = inflater.inflate(R.layout.fragment_bookings, container, false)
        val tvTitle = view.findViewById<TextView>(R.id.tvBookingsTitle)
        tvTitle.text = if (role == "FAMILY") "My Booking Requests" else "My Assigned Visits"
        return view
    }
}