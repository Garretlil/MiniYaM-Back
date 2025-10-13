package com.example.demo.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "Artist")
data class Artist(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long =0,
    val name: String
)

