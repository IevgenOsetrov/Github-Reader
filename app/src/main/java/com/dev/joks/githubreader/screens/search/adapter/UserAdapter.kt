package com.dev.joks.githubreader.screens.search.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dev.joks.githubreader.GithubReaderApp
import com.dev.joks.githubreader.R
import com.dev.joks.githubreader.service.model.User
import com.dev.joks.githubreader.utils.formatToPlaces
import kotlinx.android.synthetic.main.list_item_user.view.*

class UsersAdapter(
    private val context: Context?,
    private val items: MutableList<User>
) : RecyclerView.Adapter<UsersAdapter.ViewHolder>() {

    var onItemClick: ((User) -> Unit)? = null

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(items: List<User>) {
        this.items.addAll(items)
        notifyDataSetChanged()
    }

    fun clear() {
        this.items.clear()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item_user, p0, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(items[adapterPosition])
            }
        }

        fun bind(item: User) {
            Glide.with(GithubReaderApp.instance)
                .load(item.avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(itemView.ivAvatar)

            with(itemView) {
                tvUsername.text = item.login
                tvScore.text = item.score.formatToPlaces(3)
            }
        }
    }
}