package com.arash.altafi.dragdropsample.sample5

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.dragdropsample.databinding.ItemDragAndDrop1Binding

class DragAndDropAdapter1(private val activity: Sample5Activity) :
    RecyclerView.Adapter<DragAndDropAdapter1.DragAndDropViewHolder>() {

    private var emojis = listOf(
        "😀",
        "😃",
        "😄",
        "😁",
        "😆",
        "😅",
        "😂",
        "🤣",
        "☺",
        "😊",
        "😇",
        "🙂",
        "🙃",
        "😉"
    ).toMutableList()

    fun moveItem(from: Int, to: Int) {
        val fromEmoji = emojis[from]
        emojis.removeAt(from)
        if (to < from) {
            emojis.add(to, fromEmoji)
        } else {
            emojis.add(to - 1, fromEmoji)
        }
    }

    override fun getItemCount(): Int {
        return emojis.size
    }

    override fun onBindViewHolder(holder: DragAndDropViewHolder, position: Int) {
        val emoji = emojis[position]
        holder.bind(emoji)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DragAndDropViewHolder {
        val binding =
            ItemDragAndDrop1Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DragAndDropViewHolder(binding)
    }

    inner class DragAndDropViewHolder(private val binding: ItemDragAndDrop1Binding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(text: String) = binding.apply {
            binding.textView.text = text
            handleView.setOnTouchListener { _, event ->
                if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                    activity.startDragging(this@DragAndDropViewHolder)
                }
                return@setOnTouchListener true
            }
        }
    }

}