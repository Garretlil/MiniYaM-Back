package com.example.demo.model.entity

import jakarta.persistence.*
import java.time.LocalDateTime

fun isExpired(expiresAt: LocalDateTime) : Boolean{
    return LocalDateTime.now().isAfter(expiresAt);
}

@Entity
@Table(name="temp_codes")
data class TempCodes(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long=0,
    val email: String,
    val hashCode:String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime,
    val used: Boolean

)