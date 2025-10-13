package com.example.demo

import com.example.demo.model.entity.Track
import com.example.demo.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TrackRepository: JpaRepository<Track,Long>{
    @Query(
        value = "SELECT * FROM track WHERE title ILIKE CONCAT('%', :query, '%')",
        nativeQuery = true
    )
    fun searchTracksByTitle(@Param("query") query: String): List<Track>
}

interface UserRepository: JpaRepository<User,Long>{

    fun findByEmail(email: String): User?
}
