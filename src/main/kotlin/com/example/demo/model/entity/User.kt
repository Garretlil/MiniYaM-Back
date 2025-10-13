package com.example.demo.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "Users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    val email: String,
    val password: String,
    val token: String,
) {
    constructor() : this(0, "", "", "", "")
}
