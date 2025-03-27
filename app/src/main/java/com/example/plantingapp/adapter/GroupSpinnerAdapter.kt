package com.example.plantingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.MenuPopupWindow.MenuDropDownListView
import com.example.plantingapp.R
import com.example.plantingapp.item.UniversalTodoItem.GroupBasicItem


class GroupSpinnerAdapter(
    context: Context,
    layoutRes: Int = R.layout.item_group_spinner,
    val items: MutableList<GroupBasicItem> = mutableListOf(
        GroupBasicItem(
            -1,
            "- 未选择 -"
        )
    ),
    private val itemCallback: (Int) -> Unit
) : ArrayAdapter<GroupBasicItem>(context, layoutRes, items) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_group_spinner, parent, false)
        val textView = view.findViewById<TextView>(R.id.group_name)
        view.setOnClickListener {
            itemCallback(position)
        }
        textView.text = items[position].name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

}