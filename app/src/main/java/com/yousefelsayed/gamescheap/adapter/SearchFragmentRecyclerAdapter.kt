package com.yousefelsayed.gamescheap.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.imageview.ShapeableImageView
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.model.GameItemModel
import com.yousefelsayed.gamescheap.model.SearchArrayModel
import com.yousefelsayed.gamescheap.model.SearchItemModel
import com.yousefelsayed.gamescheap.util.GlideApp

class SearchFragmentRecyclerAdapter: RecyclerView.Adapter<SearchFragmentRecyclerAdapter.ViewHolder>() {

    var onItemClick: ((SearchItemModel) -> Unit)? = null
    //DiffUtil
    private val diffCallback = object: DiffUtil.ItemCallback<SearchItemModel>(){
        override fun areItemsTheSame(oldItem: SearchItemModel, newItem: SearchItemModel): Boolean {
            return oldItem.gameID == newItem.gameID
        }

        override fun areContentsTheSame(oldItem: SearchItemModel, newItem: SearchItemModel): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this,diffCallback)
    fun submitList(productsList: ArrayList<SearchItemModel>){
        if (productsList.isEmpty()) return
        differ.submitList(productsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFragmentRecyclerAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_games,parent,false)
        return ViewHolder(v)
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onBindViewHolder(holder: SearchFragmentRecyclerAdapter.ViewHolder, position: Int) {
        if (position > differ.currentList.size) return
        val currentItem = differ.currentList[position]
        GlideApp.with(holder.gameImage).asBitmap().load(currentItem.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .apply(RequestOptions().override(holder.gameImage.width,holder.gameImage.height))
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .listener(object: RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>, isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Bitmap, model: Any, target: Target<Bitmap>?, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                    if (!resource.isRecycled){
                        val palette = Palette.from(resource).generate()
                        val transparentDominantColor = ColorUtils.setAlphaComponent(palette.getDominantColor(Color.BLACK),180)
                        holder.gameBackground.setImageBitmap(resource)
                        holder.gameBackground.setColorFilter(transparentDominantColor)
                    }
                    return false
                }
            }).into(holder.gameImage)
        holder.gameTitle.text = currentItem.external
        holder.gameDescription.text = " "
        holder.gameCurrentPrice.text = "$${currentItem.cheapest}"
        holder.parentView.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }
    override fun onViewRecycled(holder: ViewHolder) {
        GlideApp.with(holder.gameImage).clear(holder.gameImage)
        holder.gameBackground.setImageBitmap(null)
        super.onViewRecycled(holder)
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val parentView: ConstraintLayout = itemView.findViewById(R.id.gamesRecyclerViewParentView)
        val gameBackground: ShapeableImageView = itemView.findViewById(R.id.gamesRecyclerViewItemBackground)
        val gameTitle: TextView = itemView.findViewById(R.id.gameItemTitle)
        val gameDescription: TextView = itemView.findViewById(R.id.gameItemDetails)
        val gameCurrentPrice: TextView = itemView.findViewById(R.id.gameItemCurrentPrice)
        val gameImage: ShapeableImageView = itemView.findViewById(R.id.gameItemImage)
    }
}