package ru.sergean.nasaapp.presentation.ui.home.items

import android.graphics.Rect
import android.view.View
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import ru.sergean.nasaapp.R

class HomeSearchDecorator(
    private val leftDivider: Int,
    private val topDivider: Int,
    private val rightDivider: Int,
    private val bottomDivider: Int,
) : RecyclerView.ItemDecoration() {

    constructor(horizontalDivider: Int, verticalDivider: Int) :
            this(horizontalDivider, verticalDivider, horizontalDivider, verticalDivider)

    constructor(divider: Int) : this(divider, divider, divider, divider)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        if (parent.getChildViewHolder(view).itemViewType != R.layout.item_search) return

        outRect.set(leftDivider, topDivider, rightDivider, bottomDivider)

    }
}