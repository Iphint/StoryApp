package com.arifin.newest.view.data

import com.arifin.newest.data.response.ListStoryItem

object DataDummy {
    fun generateDummyListStoryItem(): List<ListStoryItem> {
        val items: MutableList<ListStoryItem> = arrayListOf()
        for (i in 0..100) {
            val story = ListStoryItem(
                i.toString(),
                "Muhammad Arifin $i",
                "masih pemula banget kakak $i",
                "https://story-api.dicoding.dev/images/stories/photos-1684644229822_94LZXQMg.jpg $i",
                "2023-05-21T04:43:49.824Z $i",
                -7.9466072,
                112.6546637
            )
            items.add(story)
        }
        return items
    }
}