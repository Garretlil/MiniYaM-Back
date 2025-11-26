package com.example.demo.model.entity

import com.example.demo.model.entity.User
import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*

@Entity
@Table(name = "track")
class Track(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String = "",
    val artist: String = "",
    val url: String = "",
    val duration: Int? = null,
    val imageUrl: String? = null,

    @OneToMany(mappedBy = "track", cascade = [CascadeType.ALL], orphanRemoval = true)
    @JsonIgnore
    val likes: MutableList<Like> = mutableListOf()
) {
    constructor() : this(0, "", "", "", null, null, mutableListOf())
}



