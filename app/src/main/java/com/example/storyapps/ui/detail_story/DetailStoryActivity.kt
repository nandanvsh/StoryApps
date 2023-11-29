package com.example.storyapps.ui.detail_story

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.storyapps.data.helper.DetailData
import com.example.storyapps.databinding.ActivityDetailBinding

class DetailStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backBtn.setOnClickListener{
            onBackPressed()
        }

        val detailData = if (Build.VERSION.SDK_INT >= 33) {
            intent.getSerializableExtra(EXTRA_ID, DetailData::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra(EXTRA_ID) as DetailData
        }


        binding.tvDetailName.text = detailData?.nama
        binding.tvDetailDescription.text = detailData?.description


        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transform(CenterCrop(), RoundedCorners(16))

        Glide.with(binding.root)
            .load(detailData?.image)
            .apply(requestOptions)
            .into(binding.ivDetailPhoto)
    }
    companion object{
        const val EXTRA_ID = "extra_id"
    }
}