package com.example.messmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.messmanagement.databinding.ItemMessPlanBinding
import com.example.messmanagement.models.MessPlan

class MessPlanAdapter(
    private val messPlans: List<MessPlan>,
    private val onPlanBooked: (MessPlan) -> Unit
) : RecyclerView.Adapter<MessPlanAdapter.MessPlanViewHolder>() {

    inner class MessPlanViewHolder(private val binding: ItemMessPlanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(messPlan: MessPlan) {
            binding.apply {
                planNameTextView.text = messPlan.name
                planDaysTextView.text = "Duration: ${messPlan.totalDays} days"
                planPriceTextView.text = "Price: ₹${messPlan.price}"

                bookPlanButton.setOnClickListener {
                    onPlanBooked(messPlan)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessPlanViewHolder {
        val binding = ItemMessPlanBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MessPlanViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessPlanViewHolder, position: Int) {
        holder.bind(messPlans[position])
    }

    override fun getItemCount() = messPlans.size
}