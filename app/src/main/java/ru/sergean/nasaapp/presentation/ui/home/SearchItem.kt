package ru.sergean.nasaapp.presentation.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.databinding.ItemSearchBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.BaseViewHolder
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.base.adapter.ItemFingerprint

data class SearchItem(val initText: String): Item

class SearchItemFingerprint(
    private val scope: CoroutineScope,
    private val onQueryChanged: (String) -> Unit,
) : ItemFingerprint<ItemSearchBinding, SearchItem> {

    override fun isRelativeItem(item: Item) = item is SearchItem

    override fun getLayoutId() = R.layout.item_search

    override fun getViewHolder(
        layoutInflater: LayoutInflater,
        parent: ViewGroup
    ): BaseViewHolder<ItemSearchBinding, SearchItem> {
        val binding = ItemSearchBinding.inflate(layoutInflater, parent, false)
        return SearchViewHolder(binding, scope, onQueryChanged)
    }

    override fun getDiffUtil() = diffUtil

    private val diffUtil = object : DiffUtil.ItemCallback<SearchItem>() {

        override fun areItemsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem.initText == newItem.initText
        }

        override fun areContentsTheSame(oldItem: SearchItem, newItem: SearchItem): Boolean {
            return oldItem == newItem
        }
    }
}

class SearchViewHolder(
    binding: ItemSearchBinding,
    private val scope: CoroutineScope,
    private val onQueryChanged: (String) -> Unit,
) : BaseViewHolder<ItemSearchBinding, SearchItem>(binding) {

    private var currentQuery = ""

    init {
        binding.searchEditText.doOnTextChanged { text, _, _, _ ->
            val query = text.toString()
            if (currentQuery != query) {
                currentQuery = query
                scope.launch {
                    delay(timeMillis = 1000)
                    if (currentQuery != query) {
                        return@launch
                    }
                    onQueryChanged(currentQuery)
                }
            }
        }
    }

    override fun onBind(item: SearchItem) {
        super.onBind(item)
        binding.searchEditText.setText(item.initText)
    }
}