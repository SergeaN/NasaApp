package ru.sergean.nasaapp.presentation.ui.home.items

import android.view.LayoutInflater
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.DiffUtil
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import ru.sergean.nasaapp.R
import ru.sergean.nasaapp.databinding.ItemSearchBinding
import ru.sergean.nasaapp.presentation.ui.base.adapter.BaseViewHolder
import ru.sergean.nasaapp.presentation.ui.base.adapter.Item
import ru.sergean.nasaapp.presentation.ui.base.adapter.ItemFingerprint
import ru.sergean.nasaapp.utils.EditTextWatcher

data class SearchItem(var initText: String) : Item

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
    scope: CoroutineScope,
    onQueryChanged: (String) -> Unit,
) : BaseViewHolder<ItemSearchBinding, SearchItem>(binding) {

    init {
        val editTextWatcher = EditTextWatcher(scope)
        binding.run {
            searchEditText.doOnTextChanged { text, _, _, _ ->
                item.initText = text.toString()
                editTextWatcher.watch(text, onQueryChanged)
            }
            searchEditText.onFocusChangeListener = OnFocusChangeListener { _, isFocused ->
                searchInput.setHint(isFocused)
            }
        }
    }

    override fun onBind(item: SearchItem) {
        super.onBind(item)
        binding.searchEditText.setText(item.initText)
        binding.searchInput.setHint()
    }

    private fun TextInputLayout.setHint(isFocused: Boolean = false) {
        hint = when {
            isFocused -> ""
            binding.searchEditText.text?.isNotEmpty() == true -> ""
            else -> resources.getString(R.string.search_hint)
        }
    }
}