package com.arash.altafi.dragdropsample.sample4.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.dragdropsample.databinding.UserItemBinding
import com.arash.altafi.dragdropsample.sample4.interfaces.ItemTouchHelperAdapter
import com.arash.altafi.dragdropsample.sample4.models.User
import java.util.*
import kotlin.collections.ArrayList

class RvAdapter(var userList: ArrayList<User>) : RecyclerView.Adapter<RvAdapter.Vh>(),
    ItemTouchHelperAdapter {

    inner class Vh(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun onBind(user: User) {
            binding.name.text = user.name
            binding.phoneNumber.text = user.phoneNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Vh {
        return Vh(UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: Vh, position: Int) {
        holder.onBind(userList[position])
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(userList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(userList, i, i - 1)
            }
        }

        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemDismiss(position: Int) {
        userList.removeAt(position)
        notifyItemRemoved(position)
    }

}