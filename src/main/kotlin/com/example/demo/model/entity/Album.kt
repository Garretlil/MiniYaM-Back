package com.example.demo.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "Album")
data class Album(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long=0,
    val title: String,

    @ManyToOne
    @JoinTable(name="artist_id")
    val artist: Artist

)