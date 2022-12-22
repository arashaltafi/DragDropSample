package com.arash.altafi.dragdropsample.sample6

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.dragdropsample.databinding.ItemDragAndDrop2Binding

class DragAndDropAdapter2 : RecyclerView.Adapter<DragAndDropAdapter2.DragAndDrop2ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DragAndDrop2ViewHolder {
        val binding =
            ItemDragAndDrop2Binding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DragAndDrop2ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DragAndDrop2ViewHolder, position: Int) {
        val movieItem = differ.currentList[position]
        holder.bindView(movieItem)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private val differCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, differCallback)

    fun moveItem(fromPosition: Int, toPosition: Int) {
        val list = differ.currentList.toMutableList()
        val fromItem = list[fromPosition]
        list.removeAt(fromPosition)
        if (toPosition < fromPosition) {
            list.add(toPosition + 1, fromItem)
        } else {
            list.add(toPosition - 1, fromItem)
        }
        differ.submitList(list)
    }

    inner class DragAndDrop2ViewHolder(private val binding: ItemDragAndDrop2Binding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(item: User) = binding.apply {
            textTitle.text = item.name
            textDesc.text = item.location
        }
    }
}
