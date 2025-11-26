package com.example.demo

import com.example.demo.model.entity.Like
import com.example.demo.model.entity.Track
import com.example.demo.model.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

interface TrackRepository: JpaRepository<Track,Long>{
    @Query(
        value = "SELECT * FROM track WHERE title ILIKE CONCAT('%', :query, '%')",
        nativeQuery = true
    )
    fun searchTracksByTitle(@Param("query") query: String): List<Track>
}

@Repository
interface UserRepository : JpaRepository<User, Long> {
    fun findByToken(token: String): User?
    fun findByEmail(email: String): User?
}

@Repository
interface LikeRepository : JpaRepository<Like, Long> {
    fun findByUserIdAndTrackId(userId: Long, trackId: Long): Like?
    fun existsByUserIdAndTrackId(userId: Long, trackId: Long): Boolean
    fun findByUserId(userId: Long): List<Like>
}