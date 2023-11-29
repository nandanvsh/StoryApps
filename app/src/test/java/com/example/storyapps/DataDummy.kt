package com.example.storyapps

import com.example.storyapps.data.response.ListStoryItem


object DataDummy {

            fun generateDummyQuoteResponse(): List<ListStoryItem> {
                val items: MutableList<ListStoryItem> = arrayListOf()
                for (i in 0..100) {
                    val quote = ListStoryItem(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/3/3f/Placeholder_view_vector.svg/310px-Placeholder_view_vector.svg.png",
                        "2012-12-08T06:34:19.598Z",
                        "name $i",
                        "description $i",
                        "lon $i",
                        i.toString(),
                        "0",



                    )
                    items.add(quote)
                }
                return items
            }
}