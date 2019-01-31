package com.dev.joks.githubreader.screens.details.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dev.joks.githubreader.R
import com.dev.joks.githubreader.service.model.Repository
import com.dev.joks.githubreader.utils.gone
import kotlinx.android.synthetic.main.list_item_repository.view.*

class RepoAdapter(
    private val context: Context?,
    private val items: MutableList<Repository>
) : RecyclerView.Adapter<RepoAdapter.ViewHolder>() {

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(items: List<Repository>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_repository, p0, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Repository) {
            with(itemView) {
                tvName.text = item.name
                if (item.language != null) {
                    tvLanguage.text = context.getString(R.string.language, item.language)
                } else {
                    tvLanguage.gone()
                }
                tvForksCount.text = item.forks.toString()
                tvStarsCount.text = item.stars.toString()
            }
        }
    }
}