package com.example.messmanagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.messmanagement.databinding.ItemFeedbackBinding
import com.example.messmanagement.models.Feedback

class FeedbackAdapter(
    private val feedbacks: List<Feedback>
) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    inner class FeedbackViewHolder(private val binding: ItemFeedbackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(feedback: Feedback) {
            with(binding) {
                // Feedback Message
                messageTextView.text = feedback.message ?: "No message"

                // Status
                val statusText = feedback.status ?: "Unknown"
                statusTextView.text = "Status: $statusText"

                // Date Formatting
                val createdAt = feedback.createdAt?.takeIf { it.size >= 3 }?.let {
                    "${it[2]}/${it[1]}/${it[0]}"
                } ?: "N/A"
                dateTextView.text = "Date: $createdAt"

                // Admin Response Handling
                if (!feedback.adminResponse.isNullOrEmpty()) {
                    adminResponseTextView.visibility = View.VISIBLE
                    adminResponseTextView.text = "Admin Response: ${feedback.adminResponse}"
                } else {
                    adminResponseTextView.visibility = View.GONE
                }

                // Status Color Handling
                val statusColor = when (feedback.status) {
                    "RESOLVED" -> ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
                    "PENDING" -> ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
                    else -> ContextCompat.getColor(itemView.context, android.R.color.darker_gray)
                }
                statusTextView.setTextColor(statusColor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val binding = ItemFeedbackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedbackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        holder.bind(feedbacks[position])
    }

    override fun getItemCount(): Int = feedbacks.size
}
