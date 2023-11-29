package com.example.storyapps.ui.story_list

import android.animation.ArgbEvaluator
import android.animation.TimeAnimator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.storyapps.R
import com.example.storyapps.data.helper.DetailData
import com.example.storyapps.data.response.ListStoryItem
import com.example.storyapps.databinding.ItemCardBinding
import com.example.storyapps.ui.detail_story.DetailStoryActivity
import com.squareup.picasso.Picasso


class StoryAdapter : PagingDataAdapter<ListStoryItem, StoryAdapter.TheViewHolder>(DIFF_CALLBACK) {
     private lateinit var onItemClickCallback: OnItemClickCallback
    private lateinit var binding: ItemCardBinding

     fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
         this.onItemClickCallback = onItemClickCallback
     }
     interface OnItemClickCallback {
         fun onItemClicked(data: ListStoryItem)
     }

    companion object{
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    class TheViewHolder(val binding: ItemCardBinding, private val gradient: GradientDrawable, private val animator: ValueAnimator) : RecyclerView.ViewHolder(binding.root){
        fun bind(review: ListStoryItem){

            binding.itemName.text = "${review.name}"
            binding.deskCard.text = "${review.description}"
            Picasso.get().load(review.photoUrl).into(binding.ivItemPhoto)

            Glide.with(binding.root)
                .load(review.photoUrl)
                .placeholder(gradient)
                .listener(
                    object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>,
                            isFirstResource: Boolean
                        ) : Boolean {
                            animator.end()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable,
                            model: Any,
                            target: Target<Drawable>?,
                            dataSource: DataSource,
                            isFirstResource: Boolean
                        ): Boolean {
                            animator.end()
                            return false
                        }

                    }
                )
                .into(binding.ivItemPhoto)

            binding.itemCard.setOnClickListener{
                val intentDetail = Intent(itemView.context, DetailStoryActivity::class.java)
                intentDetail.putExtra(DetailStoryActivity.EXTRA_ID, DetailData(nama = review.name!!, image = review.photoUrl!!, description = review.description!! ))
                val optionsCompat: ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(binding.ivItemPhoto, "imageStory"),
                        Pair(binding.itemName, "name"),
                        Pair(binding.deskCard, "description"),
                    )
                itemView.context.startActivity(intentDetail, optionsCompat.toBundle())
            }
        }
    }

     override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TheViewHolder {
         binding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)

         val from = ContextCompat.getColor(binding.root.context, R.color.gray)
         val to = ContextCompat.getColor(binding.root.context, R.color.gray)
         binding.ivItemPhoto.setImageDrawable(
             GradientDrawable( GradientDrawable.Orientation.LEFT_RIGHT,
                 intArrayOf(
                     to, from
                 ),
             ))
         val gradient = binding.ivItemPhoto.drawable as GradientDrawable

         val evaluator = ArgbEvaluator()
         val animator = TimeAnimator.ofFloat(0.0f, 1.0f)

         animator.duration = 1500
         animator.repeatCount = ValueAnimator.INFINITE
         animator.repeatMode = ValueAnimator.REVERSE
         animator.addUpdateListener {
             val fraction = it.animatedFraction
             val newStart = evaluator.evaluate(fraction, from, to) as Int
             val newEnd = evaluator.evaluate(fraction, to, from) as Int

             gradient.colors = intArrayOf(newStart, newEnd)
         }

         animator.start()
         return TheViewHolder(binding, gradient, animator)
     }

    override fun onBindViewHolder(holder: TheViewHolder, position: Int) {
        val story = getItem(position)

        if (story != null) {
            holder.bind(story)
        }
    }
}