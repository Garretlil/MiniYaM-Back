package com.example.demo.controller
import com.example.demo.TrackRepository
import com.example.demo.model.entity.Track
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.io.RandomAccessFile

data class RequestSearchMusic(
    val searchQuery: String
)

@RestController
class PlaylistController(private val trackRepository: TrackRepository) {
    @GetMapping("/getTracks")
    fun getTracks(): List<Track> =trackRepository.findAll()
    @PostMapping("/searchTracks")
    fun searchTracks(
        @RequestBody request: RequestSearchMusic
    ): List<Track> {
        try {
            val tracks=trackRepository.searchTracksByTitle(request.searchQuery)
            return tracks
        }
        catch (e: Exception) {
            e.printStackTrace();
            return emptyList()
        }
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

