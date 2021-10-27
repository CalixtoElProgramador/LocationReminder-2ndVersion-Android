package com.example.android.locationreminder.bindings.reminder

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.android.locationreminder.data.model.Reminder
import com.example.android.locationreminder.ui.reminder.list.adapter.RemindersAdapter

/**
 * [BindingAdapter]s for the [Reminder]s list.
 */

@BindingAdapter("app:items")
fun RecyclerView.setItems(items: List<Reminder>?) {
    items?.let {
        (adapter as RemindersAdapter).submitList(items)
    }
}