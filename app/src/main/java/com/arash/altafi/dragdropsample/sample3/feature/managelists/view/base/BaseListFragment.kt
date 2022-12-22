package com.arash.altafi.dragdropsample.sample3.feature.managelists.view.base

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.*
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import com.arash.altafi.dragdropsample.R
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemSwipeListener
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnListScrollListener
import com.arash.altafi.dragdropsample.sample3.config.local.ListFragmentType
import com.arash.altafi.dragdropsample.sample3.config.local.currentListFragmentConfig
import com.arash.altafi.dragdropsample.sample3.config.local.currentListFragmentType
import com.arash.altafi.dragdropsample.sample3.data.model.IceCream
import com.arash.altafi.dragdropsample.sample3.data.source.IceCreamRepository
import com.arash.altafi.dragdropsample.sample3.data.source.base.BaseRepository
import com.arash.altafi.dragdropsample.sample3.feature.managelists.IceCreamListAdapter
import com.arash.altafi.dragdropsample.sample3.feature.managelists.view.GridListFragment
import com.arash.altafi.dragdropsample.sample3.feature.managelists.view.HorizontalListFragment
import com.arash.altafi.dragdropsample.sample3.feature.managelists.view.VerticalListFragment
import com.arash.altafi.dragdropsample.sample3.util.Logger
import com.google.android.material.snackbar.Snackbar

/**
 * The base implementation of a fragment that displays a list of ice creams.
 */
abstract class BaseListFragment : Fragment() {

    private val adapter = IceCreamListAdapter()
    private val repository = IceCreamRepository.getInstance()

    private var _binding: ViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var list: DragDropSwipeRecyclerView
    private lateinit var loadingIndicator: ProgressBar

    protected abstract val optionsMenuId: Int

    private val onItemSwipeListener = object : OnItemSwipeListener<IceCream> {
        override fun onItemSwiped(position: Int, direction: OnItemSwipeListener.SwipeDirection, item: IceCream): Boolean {
            when (direction) {
                OnItemSwipeListener.SwipeDirection.RIGHT_TO_LEFT -> onItemSwipedLeft(item, position)
                OnItemSwipeListener.SwipeDirection.LEFT_TO_RIGHT -> onItemSwipedRight(item, position)
                OnItemSwipeListener.SwipeDirection.DOWN_TO_UP -> onItemSwipedUp(item, position)
                OnItemSwipeListener.SwipeDirection.UP_TO_DOWN -> onItemSwipedDown(item, position)
            }

            return false
        }
    }

    private val onItemDragListener = object : OnItemDragListener<IceCream> {
        override fun onItemDragged(previousPosition: Int, newPosition: Int, item: IceCream) {
            Logger.log("$item is being dragged from position $previousPosition to position $newPosition")
        }

        override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: IceCream) {
            if (initialPosition != finalPosition) {
                Logger.log("$item moved (dragged from position $initialPosition and dropped in position $finalPosition)")

                // Change item position inside the repository
                repository.removeItem(item)
                repository.insertItem(item, finalPosition)
            } else {
                Logger.log("$item dragged from (and also dropped in) the position $initialPosition")
            }
        }
    }

    private val onListScrollListener = object : OnListScrollListener {
        override fun onListScrollStateChanged(scrollState: OnListScrollListener.ScrollState) {
            // Call commented out to avoid saturating the log
            //Logger.log("List scroll state changed to $scrollState")
        }

        override fun onListScrolled(scrollDirection: OnListScrollListener.ScrollDirection, distance: Int) {
            // Call commented out to avoid saturating the log
            //Logger.log("List scrolled $distance pixels $scrollDirection")
        }
    }

    private val onItemAddedListener = object : BaseRepository.OnItemAdditionListener<IceCream> {
        override fun onItemAdded(item: IceCream, position: Int) {
            // Add the item to the adapter's data set if necessary
            if (!adapter.dataSet.contains(item)) {
                Logger.log("Added new item $item")

                adapter.insertItem(position, item)

                // We scroll to the position of the added item (positions match in both adapter and repository)
                list.smoothScrollToPosition(position)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {

        // Set root view for the fragment and find the views
        _binding = inflateViewBinding(inflater,container)
        list = binding.root.findViewById(R.id.list)
        loadingIndicator = binding.root.findViewById(R.id.loading_indicator)

        // Set adapter and listeners
        list.adapter = adapter
        list.swipeListener = onItemSwipeListener
        list.dragListener = onItemDragListener
        list.scrollListener = onListScrollListener

        // Finish list setup
        setupListLayoutManager(list)
        setupListOrientation(list)
        setupListItemLayout(list)
        setupLayoutBehindItemLayoutOnSwiping(list)
        setupFadeItemLayoutOnSwiping(list)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected abstract fun inflateViewBinding(inflater: LayoutInflater, container: ViewGroup?): ViewBinding

    protected abstract fun setupListLayoutManager(list: DragDropSwipeRecyclerView)

    protected abstract fun setupListOrientation(list: DragDropSwipeRecyclerView)

    protected abstract fun setupListItemLayout(list: DragDropSwipeRecyclerView)

    protected abstract fun setupLayoutBehindItemLayoutOnSwiping(list: DragDropSwipeRecyclerView)

    protected abstract fun setupFadeItemLayoutOnSwiping(list: DragDropSwipeRecyclerView)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = inflater.inflate(optionsMenuId, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.change_item_layout ->
                currentListFragmentConfig.isUsingStandardItemLayout = !currentListFragmentConfig.isUsingStandardItemLayout

            R.id.change_dragging_restrictions ->
                currentListFragmentConfig.isRestrictingDraggingDirections = !currentListFragmentConfig.isRestrictingDraggingDirections

            R.id.enable_disable_drawing_behind ->
                currentListFragmentConfig.isDrawingBehindSwipedItems = !currentListFragmentConfig.isDrawingBehindSwipedItems

            R.id.enable_disable_fade_on_swiping ->
                currentListFragmentConfig.isUsingFadeOnSwipedItems = !currentListFragmentConfig.isUsingFadeOnSwipedItems
        }

        // Reload the whole fragment to apply the changes
        // (notifying changes on the data set wouldn't be enough because of view recycling)
        val fragmentManager = activity?.supportFragmentManager
        if (fragmentManager != null) {
            val fragment: BaseListFragment = when (currentListFragmentType) {
                ListFragmentType.VERTICAL -> VerticalListFragment.newInstance()
                ListFragmentType.HORIZONTAL -> HorizontalListFragment.newInstance()
                ListFragmentType.GRID -> GridListFragment.newInstance()
            }
            fragmentManager.beginTransaction().apply {
                replace(R.id.content_frame, fragment, tag)
            }.commit()
        }

        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)

        updateMenuItemTitles(menu)
    }

    private fun updateMenuItemTitles(menu: Menu) {

        // Update the menu titles depending on the selected options
        for (index in 0 until menu.size()) {
            val menuItem = menu.getItem(index)
            when (menuItem.itemId) {

                R.id.change_item_layout ->
                    menuItem.title = getString(
                            if (currentListFragmentConfig.isUsingStandardItemLayout)
                                R.string.action_use_card_view_item_layout
                            else
                                R.string.action_use_default_item_layout)

                R.id.change_dragging_restrictions ->
                    menuItem.title = getString(
                            if (currentListFragmentConfig.isRestrictingDraggingDirections)
                                R.string.action_not_restrict_dragging
                            else
                                R.string.action_restrict_dragging)

                R.id.enable_disable_drawing_behind ->
                    menuItem.title = getString(
                            if (currentListFragmentConfig.isDrawingBehindSwipedItems)
                                R.string.action_not_draw_behind_swiped_items
                            else
                                R.string.action_draw_behind_swiped_items)

                R.id.enable_disable_fade_on_swiping ->
                    menuItem.title = getString(
                            if (currentListFragmentConfig.isUsingFadeOnSwipedItems)
                                R.string.action_not_reduce_alpha_on_swiping
                            else
                                R.string.action_reduce_alpha_on_swiping)
            }
        }
    }

    override fun onPause() {
        super.onPause()

        // Unsubscribe from repository changes
        repository.removeOnItemAdditionListener(onItemAddedListener)
    }

    override fun onResume() {
        super.onResume()

        // Subscribe to repository changes and then reload items
        repository.addOnItemAdditionListener(onItemAddedListener)
        reloadItems()
    }

    private fun reloadItems() {
        // Show loader view
        loadingIndicator.visibility = View.VISIBLE
        list.visibility = View.GONE

        loadData()

        // Hide loader view after a small delay to simulate real data retrieval
        Handler(Looper.getMainLooper()).postDelayed({
            loadingIndicator.visibility = View.GONE
            list.visibility = View.VISIBLE
        }, 150)
    }

    private fun loadData() {
        adapter.dataSet = repository.getAllItems()
    }

    private fun onItemSwipedLeft(item: IceCream, position: Int) {
        Logger.log("$item (position $position) swiped to the left")

        removeItem(item, position)
    }

    private fun onItemSwipedRight(item: IceCream, position: Int) {
        Logger.log("$item (position $position) swiped to the right")

        archiveItem(item, position)
    }

    private fun onItemSwipedUp(item: IceCream, position: Int) {
        Logger.log("$item (position $position) swiped up")

        archiveItem(item, position)
    }

    private fun onItemSwipedDown(item: IceCream, position: Int) {
        Logger.log("$item (position $position) swiped down")

        removeItem(item, position)
    }

    private fun removeItem(item: IceCream, position: Int) {
        Logger.log("Removed item $item")

        removeItemFromList(item, position, R.string.itemRemovedMessage)
    }

    private fun archiveItem(item: IceCream, position: Int) {
        Logger.log("Archived item $item")

        removeItemFromList(item, position, R.string.itemArchivedMessage)
    }

    private fun removeItemFromList(item: IceCream, position: Int, stringResourceId: Int) {
        repository.removeItem(item)

        val itemSwipedSnackBar = Snackbar.make(binding.root, getString(stringResourceId, item), Snackbar.LENGTH_SHORT)
        itemSwipedSnackBar.setAction(getString(R.string.undoCaps)) {
            Logger.log("UNDO: $item has been added back to the list in the position $position")

            repository.insertItem(item, position)
        }
        itemSwipedSnackBar.show()
    }
}