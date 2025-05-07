package com.example.messmanagement.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.messmanagement.databinding.ItemMealBinding
import com.example.messmanagement.models.Meal

class MealAdapter(
    private val meals: List<Meal>,
    private val onMealSelected: (Meal) -> Unit
) : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private val selectedMeals = mutableListOf<Meal>()

    inner class MealViewHolder(private val binding: ItemMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal) {
            binding.apply {
                // Meal Type
                mealTypeTextView.text = meal.mealType?.toString() ?: "Unknown Meal"

                // Menu Items
                val menuItems = meal.menuItemsList?.joinToString(", ") ?: meal.menuItems ?: "No menu items"
                menuItemsTextView.text = menuItems

                // Checkbox
                mealCheckBox.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        meal.id?.let {
                            selectedMeals.add(meal)
                            onMealSelected(meal)
                        } ?: run {
                            mealCheckBox.isChecked = false
                            Toast.makeText(
                                binding.root.context,
                                "Invalid meal selection",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        selectedMeals.remove(meal)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position])
    }

    override fun getItemCount() = meals.size

    fun getSelectedMeals(): List<Meal> {
        return selectedMeals.filter { it.id != null }
    }
}