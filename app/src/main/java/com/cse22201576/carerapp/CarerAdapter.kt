package com.cse22201576.carerapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class CarerProfile(
    val uid: String = "",
    val fullName: String = "",
    val carerType: String = "",
    val hospital: String = "",
    val bio: String = "",
    val credentials: String = "",
    val experienceYears: String = "",
    val hourlyRate: String = "",
    val ratingAvg: Double = 0.0,
    val isVerified: Boolean = false,
    val photoUrl: String = ""
)

class CarerAdapter(
    private var carers: List<CarerProfile>,
    private val onClick: (CarerProfile) -> Unit
) : RecyclerView.Adapter<CarerAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvInitial: TextView = view.findViewById(R.id.tvInitial)
        val tvName: TextView = view.findViewById(R.id.tvCarerName)
        val tvType: TextView = view.findViewById(R.id.tvCarerType)
        val tvHospital: TextView = view.findViewById(R.id.tvHospital)
        val tvExperience: TextView = view.findViewById(R.id.tvExperience)
        val tvRate: TextView = view.findViewById(R.id.tvRate)
        val tvRating: TextView = view.findViewById(R.id.tvRating)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carer_card, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val carer = carers[position]

        // show first letter of name as avatar
        holder.tvInitial.text = carer.fullName.firstOrNull()?.toString() ?: "?"
        holder.tvName.text = carer.fullName
        holder.tvType.text = carer.carerType

        // show hospital only for specialized
        if (carer.hospital.isNotEmpty()) {
            holder.tvHospital.visibility = View.VISIBLE
            holder.tvHospital.text = carer.hospital
        } else {
            holder.tvHospital.visibility = View.GONE
        }

        holder.tvExperience.text = "${carer.experienceYears} yrs exp"
        holder.tvRate.text = "৳${carer.hourlyRate}/hr"
        holder.tvRating.text = "★ ${carer.ratingAvg}"

        holder.itemView.setOnClickListener { onClick(carer) }
    }

    override fun getItemCount() = carers.size

    fun updateList(newList: List<CarerProfile>) {
        carers = newList
        notifyDataSetChanged()
    }
}