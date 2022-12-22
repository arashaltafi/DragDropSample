package com.arash.altafi.dragdropsample.sample1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.arash.altafi.dragdropsample.R
import com.arash.altafi.dragdropsample.databinding.ActivitySample1Binding
import com.jmedeisis.draglinearlayout.DragLinearLayout

class Sample1Activity : AppCompatActivity() {

    private lateinit var binding: ActivitySample1Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySample1Binding.inflate(layoutInflater)
        setContentView(binding.root)

        init()
    }

    private fun init() = binding.apply {
        for (i in 0 until dragLinearLayout.childCount) {
            val child: View = dragLinearLayout.getChildAt(i)
            dragLinearLayout.setViewDraggable(child, child)
        }

        //add view
//        val view = View.inflate(this@Sample1Activity, R.layout.activity_main, null)
//        dragLinearLayout.addDragView(view, view.findViewById(R.id.button))

        //remove view
//        dragLinearLayout.removeDragView(view)

        dragLinearLayout.setOnViewSwapListener { firstView, firstPosition, secondView, secondPosition ->
            Log.i("test123321", "firstView: $firstView")
            Log.i("test123321", "firstPosition: $firstPosition")
            Log.i("test123321", "secondView: $secondView")
            Log.i("test123321", "secondPosition: $secondPosition")
        }
    }

}