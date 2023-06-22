package solutions.mk.mobile.components

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

class AnyAdapter<E>(
    private val content: List<E>,
    /** see also [android.widget.Adapter.getView] */
    private val elemToView: (elem: E, elemPosition: Int, convertView: View?, parent: ViewGroup) -> View
) : BaseAdapter() {

    override fun getCount(): Int = content.size
    override fun getItem(position: Int): E = content[position]
    override fun getItemId(position: Int): Long = position.toLong()
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        elemToView(this.getItem(position), position, convertView, parent)
}