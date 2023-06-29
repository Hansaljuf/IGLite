package com.example.iglite.utils

import com.example.iglite.data.local.model.Story

object DataDummy {
    fun generateStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        for (i in 0..10) {
            val story = Story(
                id = i.toString(),
                photoUrl = "gambar $i",
                createdAt = "isi $i",
                name = "nama $i",
                description = "tanggal $i",
                lon = 0.0,
                lat = 0.0
            )
            items.add(story)
        }
        return items
    }
}