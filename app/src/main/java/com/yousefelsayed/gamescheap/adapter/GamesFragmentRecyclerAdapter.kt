package com.yousefelsayed.gamescheap.adapter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
import com.yousefelsayed.gamescheap.util.GlideApp

class GamesFragmentRecyclerAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    var onItemClick: ((GameItemModel) -> Unit)? = null
    //ViewTypes
    private val GAME_ITEM_VIEW = 0
    private val LOADING_ITEM_VIEW = 1
    //DiffUtil
    private val diffCallback = object: DiffUtil.ItemCallback<GameItemModel>(){
        override fun areItemsTheSame(oldItem: GameItemModel,newItem: GameItemModel): Boolean {
            return oldItem.dealID == newItem.dealID && oldItem.gameID == newItem.gameID
        }

        override fun areContentsTheSame(oldItem: GameItemModel,newItem: GameItemModel): Boolean {
            return oldItem == newItem
        }
    }
    private val differ = AsyncListDiffer(this,diffCallback)
    fun submitList(productsList: ArrayList<GameItemModel>){
        if (productsList.isEmpty()) return
        differ.submitList(productsList)
    }

    override fun onCreateViewHolder(parent: ViewGroup,viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            GAME_ITEM_VIEW -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_games,parent,false)
                ViewHolder(v)
            }
            LOADING_ITEM_VIEW -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_progressbar,parent,false)
                ProgressBarHolder(v)
            }
            else -> {
                val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recyclerview_games,parent,false)
                ViewHolder(v)
            }
        }
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        when(getItemViewType(position)){
            GAME_ITEM_VIEW -> {
                val currentItem = differ.currentList[position]
                val holder = viewHolder as ViewHolder
                GlideApp.with(holder.gameImage).asBitmap().load(currentItem.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .apply(RequestOptions().override(holder.gameImage.width,holder.gameImage.height))
                    .centerCrop()
                    .format(DecodeFormat.PREFER_RGB_565)
                    .listener(object: RequestListener<Bitmap>{
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
                holder.gameTitle.text = currentItem.title
                val discountPercentage = 100 - ((currentItem.salePrice.toFloat() / currentItem.normalPrice.toFloat()) * 100).toInt()
                holder.gameDescription.text = "${discountPercentage}% discount from $${currentItem.normalPrice}"
                holder.gameCurrentPrice.text = "$${currentItem.salePrice}"
                holder.parentView.animation = AnimationUtils.loadAnimation(holder.itemView.context,R.anim.recyclerview_animation)
                holder.parentView.setOnClickListener {
                    onItemClick?.invoke(currentItem)
                }
            }
            LOADING_ITEM_VIEW -> {
            }
        }
    }
    override fun getItemViewType(position: Int): Int {
        return if (position + 1 >= differ.currentList.size && position != 0) LOADING_ITEM_VIEW else GAME_ITEM_VIEW
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is ViewHolder){
            GlideApp.with(holder.gameImage).clear(holder.gameImage)
            holder.gameBackground.setImageBitmap(null)
        }
        super.onViewRecycled(holder)
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val parentView: ConstraintLayout = itemView.findViewById(R.id.gamesRecyclerViewParentView)
        val gameBackground: ShapeableImageView = itemView.findViewById(R.id.gamesRecyclerViewItemBackground)
        val gameTitle: TextView = itemView.findViewById(R.id.gameItemTitle)
        val gameDescription: TextView = itemView.findViewById(R.id.gameItemDetails)
        val gameCurrentPrice: TextView = itemView.findViewById(R.id.gameItemCurrentPrice)
        val gameImage: ShapeableImageView = itemView.findViewById(R.id.gameItemImage)
    }
    inner class ProgressBarHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
    }
}