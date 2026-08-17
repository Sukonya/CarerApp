package com.cse22201576.carerapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private var role = "FAMILY"

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
        val tvWelcome = view.findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = if (role == "FAMILY")
            "Find the right carer for your loved one"
        else
            "Welcome back! Here are your upcoming visits"
        return view
    }
}