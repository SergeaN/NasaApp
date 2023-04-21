package ru.sergean.nasaapp.presentation.ui.home.items

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.sergean.nasaapp.R

class HomeItemDecorator(
    private val innerHorizontalDivider: Int,
    private val innerVerticalDivider: Int,
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val viewHolder = parent.getChildViewHolder(view)
        if (viewHolder.itemViewType != R.layout.item_image) {
            return
        }

        val currentPosition = parent.getChildAdapterPosition(view).takeIf {
            it != RecyclerView.NO_POSITION
        } ?: viewHolder.oldPosition

        if (currentPosition !in 1..3) {
            outRect.top = innerVerticalDivider / 2
        }

        outRect.bottom = innerHorizontalDivider / 2

        when (currentPosition % 3) {
            0 -> {
                outRect.left = innerHorizontalDivider / 2
            }
            1 -> {
                outRect.right = innerHorizontalDivider / 2
            }
            2 -> {
                outRect.left = innerVerticalDivider / 2
                outRect.right = innerHorizontalDivider / 2
            }
        }
    }
}