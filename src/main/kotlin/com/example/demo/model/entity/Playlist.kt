package com.example.demo.model.entity

import jakarta.persistence.*

@Entity
@Table(name = "playlists")
data class Playlist(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val title: String,

    @ManyToMany
    @JoinTable(
        name = "playlist_tracks",
        joinColumns = [JoinColumn(name = "playlist_id")],
        inverseJoinColumns = [JoinColumn(name = "track_id")]
    )
    val tracks: MutableList<Track> = mutableListOf()
)
