package com.arash.altafi.dragdropsample.sample4

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.arash.altafi.dragdropsample.databinding.ActivitySample4Binding
import com.arash.altafi.dragdropsample.sample4.adapters.RvAdapter
import com.arash.altafi.dragdropsample.sample4.models.User

class Sample4Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySample4Binding
    private lateinit var adapter: RvAdapter
    private lateinit var userList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample4Binding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() {
        userList = ArrayList()
        for (i in 0..100) {
            userList.add(User("Item: $i", "Phone number: $i"))
        }

        adapter = RvAdapter(userList)
        binding.recyclerView.adapter = adapter

        val itemTouch = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
                val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
                return makeMovementFlags(dragFlags, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                adapter.onItemMove(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.onItemDismiss(viewHolder.adapterPosition)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouch)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

}