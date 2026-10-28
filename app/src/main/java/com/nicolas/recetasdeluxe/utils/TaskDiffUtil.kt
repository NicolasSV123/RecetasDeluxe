package com.nicolas.recetasdeluxe.utils

import androidx.recyclerview.widget.DiffUtil
import com.nicolas.recetasdeluxe.data.Recipe

class TaskDiffUtil(val oldList: List<Recipe>, val newList: List<Recipe>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return when{
            oldItem.id != newItem.id -> false
            oldItem.name != newItem.name -> false
            oldItem.image != newItem.image -> false
            oldItem.cuisine != newItem.cuisine -> false
            else -> true
        }
    }
}