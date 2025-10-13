package com.example.demo.model.entity

import com.example.demo.model.entity.User
import jakarta.persistence.*

@Entity
@Table(name ="track")
data class Track(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String = "",
    val artist: String = "",
    val url: String = "",
    val duration: Int? = null,
    val imageUrl: String? = null
)

