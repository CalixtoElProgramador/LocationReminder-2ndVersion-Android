package com.example.android.locationreminder.ui.reminder.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.databinding.ListItemReminderBinding

class RemindersAdapter(private val clickListener: OnClickListener) :
    ListAdapter<Reminder, RemindersAdapter.ReminderViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        return ReminderViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        val reminder = getItem(position)
        holder.bind(clickListener, reminder)
    }

    class ReminderViewHolder(private var binding: ListItemReminderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(clickListener: OnClickListener, reminder: Reminder) {
            binding.reminder = reminder
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ReminderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemReminderBinding.inflate(layoutInflater, parent, false)

                return ReminderViewHolder(binding)
            }
        }

    }
}

class DiffCallback : DiffUtil.ItemCallback<Reminder>() {
    override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id === newItem.id
    }

    override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
        return oldItem.id == newItem.id
    }
}

class OnClickListener(val clickListener: (reminder: Reminder) -> Unit) {
    fun onClick(reminder: Reminder) = clickListener(reminder)
}