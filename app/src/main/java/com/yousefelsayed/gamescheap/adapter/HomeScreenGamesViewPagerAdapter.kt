package com.yousefelsayed.gamescheap.adapter

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import com.yousefelsayed.gamescheap.R
import com.yousefelsayed.gamescheap.model.GameItemModel
import com.yousefelsayed.gamescheap.util.GlideApp

class HomeScreenGamesViewPagerAdapter(private val gamesList: ArrayList<GameItemModel>): RecyclerView.Adapter<HomeScreenGamesViewPagerAdapter.ViewHolder>() {

    var onItemClick: ((GameItemModel) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_viewpager_games,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return Int.MAX_VALUE
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = gamesList[position % gamesList.size]
        //Image loading and Dominate color finder for background
        GlideApp.with(holder.gameImage).asBitmap().load(currentItem.thumb.replace("capsule_sm_120.jpg?","header.jpg?"))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .placeholder(R.color.shimmerColor)
            .apply(RequestOptions().override(holder.gameImage.width,holder.gameImage.height))
            .centerCrop()
            .format(DecodeFormat.PREFER_RGB_565)
            .listener(object: RequestListener<Bitmap>{
                override fun onLoadFailed(e: GlideException?,model: Any?,target: Target<Bitmap>,isFirstResource: Boolean): Boolean {
                    return false
                }
                override fun onResourceReady(resource: Bitmap, model: Any,target: Target<Bitmap>?,dataSource: DataSource,isFirstResource: Boolean): Boolean {
                    if (!resource.isRecycled){
                        val palette = Palette.from(resource).generate()
                        holder.gameBackground.backgroundTintList = ColorStateList.valueOf(palette.getDominantColor(Color.BLACK))
                        if (!isBackgroundColorContrastGood(palette.getDominantColor(Color.BLACK),ContextCompat.getColor(holder.gameTitle.context,R.color.homeFragmentViewPagerTextColor))){
                            Log.d("Debug","Game black text title: ${currentItem.title}")
                            holder.gameTitle.setTextColor(ContextCompat.getColor(holder.gameTitle.context,R.color.homeFragmentViewPagerDarkTextColor))
                            holder.gameDescription.setTextColor(ContextCompat.getColor(holder.gameDescription.context,R.color.homeFragmentViewPagerDarkTextColor))
                            holder.gameCurrentPrice.setTextColor(ContextCompat.getColor(holder.gameCurrentPrice.context,R.color.homeFragmentViewPagerDarkTextColor))
                        }else {
                            holder.gameTitle.setTextColor(ContextCompat.getColor(holder.gameTitle.context,R.color.homeFragmentViewPagerTextColor))
                            holder.gameDescription.setTextColor(ContextCompat.getColor(holder.gameDescription.context,R.color.homeFragmentViewPagerTextColor))
                            holder.gameCurrentPrice.setTextColor(ContextCompat.getColor(holder.gameCurrentPrice.context,R.color.homeFragmentViewPagerTextColor))
                        }
                        holder.gameButton.strokeColor = ColorStateList.valueOf(palette.getDominantColor(Color.BLACK))
                    }
                    return false
                }
            }).into(holder.gameImage)
        holder.gameTitle.text = currentItem.title
        val discountPercentage = 100 - ((currentItem.salePrice.toFloat() / currentItem.normalPrice.toFloat()) * 100).toInt()
        holder.gameDescription.text = "${discountPercentage}% discount from $${currentItem.normalPrice}"
        holder.gameCurrentPrice.text = "$${currentItem.salePrice}"
        holder.gameButton.setOnClickListener {
            onItemClick?.invoke(currentItem)
        }
    }

    private fun isBackgroundColorContrastGood(imageDominantColor: Int,textColor: Int): Boolean{
        return ColorUtils.calculateContrast(textColor,imageDominantColor) > 2.5
    }

    inner class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        val gameBackground: View = itemView.findViewById(R.id.gamesItemBackground)
        val gameTitle: TextView = itemView.findViewById(R.id.gamesItemTitle)
        val gameDescription: TextView = itemView.findViewById(R.id.gamesItemDescription)
        val gameCurrentPrice: TextView = itemView.findViewById(R.id.gamesItemCurrentPrice)
        val gameImage: ShapeableImageView = itemView.findViewById(R.id.gameItemImage)
        val gameButton: MaterialButton = itemView.findViewById(R.id.gameItemViewButton)
    }
}