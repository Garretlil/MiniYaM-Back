package com.example.demo.controller
import com.example.demo.LikeRepository
import com.example.demo.TrackRepository
import com.example.demo.UserRepository
import com.example.demo.model.entity.Like
import com.example.demo.model.entity.Track
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.transaction.Transactional
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.RandomAccessFile
import java.util.Collections.emptyList


@Service
@Transactional
class TrackService(
    private val trackRepository: TrackRepository,
    private val likeRepository: LikeRepository,
    private val userRepository: UserRepository,
) {
    fun getUserIdFromToken(authHeader: String?): Long? {
        val token = authHeader?.removePrefix("Bearer")?.trim() ?: return null
        return userRepository.findByToken(token)?.id
    }

    fun getAllTracksDTO(userId: Long?): List<TrackResponse> {
        return trackRepository.findAll().map { track ->
            TrackResponse(
                id = track.id,
                title = track.title,
                artist = track.artist,
                url = track.url,
                duration = track.duration,
                imageUrl = track.imageUrl,
                liked = userId?.let { likeRepository.existsByUserIdAndTrackId(it, track.id) } ?: false
            )
        }
    }

    fun getLikedTracksDTO(userId: Long): List<TrackResponse> {
        return likeRepository.findByUserId(userId)
            .mapNotNull { like ->
                val t = like.track ?: return@mapNotNull null
                TrackResponse(
                    id = t.id,
                    title = t.title,
                    artist = t.artist,
                    url = t.url,
                    duration = t.duration,
                    imageUrl = t.imageUrl,
                    liked = true
                )
            }
    }

    fun getAllTracks(): List<Track> = trackRepository.findAll()

    fun toggleLike(userId: Long, trackId: Long): LikeResponse {
        val existingLike = likeRepository.findByUserIdAndTrackId(userId, trackId)

        return if (existingLike != null) {
            likeRepository.delete(existingLike)
            LikeResponse(trackId, "Like removed", false)
        } else {
            val user = userRepository.findById(userId).orElseThrow()
            val track = trackRepository.findById(trackId).orElseThrow()

            val like = Like(user = user, track = track)
            likeRepository.save(like)

            LikeResponse(trackId, "Track liked successfully", true)
        }
    }

    fun getLikedTracks(userId: Long): List<Track> =
        likeRepository.findByUserId(userId).map { it.track!! }

    fun getLikeStatus(userId: Long?, trackId: Long): Boolean =
        userId?.let { likeRepository.existsByUserIdAndTrackId(it, trackId) } ?: false
}



data class TrackResponse(
    val id: Long,
    val title: String,
    val artist: String,
    val url: String,
    val duration: Int?,
    val imageUrl: String?,
    val liked: Boolean = false
)

data class LikeResponse(
    val trackId: Long,
    val message: String,
    val liked: Boolean
)

data class RequestSearchMusic(
    val searchQuery: String
)

@RestController
class TrackController(
    private val trackService: TrackService
) {

    @GetMapping("/getTracks")
    fun getTracks(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<List<TrackResponse>> {
        val userId = trackService.getUserIdFromToken(authHeader)
            ?: return ResponseEntity.status(401).body(emptyList())

        val response = trackService.getAllTracksDTO(userId)
        val t =5
        return ResponseEntity.ok(response)
    }

    @GetMapping("/likedTracks")
    fun getLikedTracks(@RequestHeader("Authorization") authHeader: String?): ResponseEntity<List<TrackResponse>> {
        val userId = trackService.getUserIdFromToken(authHeader)
            ?: return ResponseEntity.status(401).body(emptyList())
        val response = trackService.getLikedTracksDTO(userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/likeTrack/{id}")
    fun likeTrack(
        @PathVariable id: Long,
        @RequestHeader("Authorization") authHeader: String?
    ): ResponseEntity<LikeResponse> {
        val userId = trackService.getUserIdFromToken(authHeader)
            ?: return ResponseEntity.status(401).body(LikeResponse(id, "User not found or no auth header", false))

        val result = trackService.toggleLike(userId, id)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/{id}/like-status")
    fun getLikeStatus(@PathVariable id: Long, @RequestHeader("Authorization") authHeader: String?): ResponseEntity<Map<String, Any>> {
        val userId = trackService.getUserIdFromToken(authHeader)
        val liked = trackService.getLikeStatus(userId, id)
        return ResponseEntity.ok(mapOf("liked" to liked))
    }
}



@RestController
class StreamTrackController(private val trackRepository: TrackRepository){

    @GetMapping("/streamTrack/{id}")
    fun streamTrack(
        @PathVariable id:Long,
        response: HttpServletResponse,
        request: HttpServletRequest
    ) {
        val track = trackRepository.findAll().find { it.id==id }
        val file = File("src/main/resourses/static${track!!.url}")
        val length = file.length()
        val rangeHeader = request.getHeader("Range")
        val bytes = rangeHeader.removePrefix("bytes=").split("-")
        val start = bytes[0].toLong()
        val end = if (bytes.size > 1 && bytes[1].isNotEmpty()) bytes[1].toLong() else length - 1
        val contentLength = end - start + 1

        response.status = 206
        response.setHeader("Content-Range", "bytes $start-$end/$length")
        response.setHeader("Content-Length", contentLength.toString())
        response.contentType = "audio/mpeg"

        RandomAccessFile(file, "r").use { raf ->
            raf.seek(start)
            val buffer = ByteArray(4096)
            var remaining = contentLength
            while (remaining > 0) {
                val read = raf.read(buffer, 0, minOf(buffer.size.toLong(), remaining).toInt())
                if (read == -1) break
                response.outputStream.write(buffer, 0, read)
                remaining -= read
            }
        }
    }

}

