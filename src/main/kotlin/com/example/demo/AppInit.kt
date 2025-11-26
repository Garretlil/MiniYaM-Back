package com.example.demo

import com.example.demo.model.entity.Track
import org.springframework.boot.CommandLineRunner
import com.mpatric.mp3agic.Mp3File
import org.springframework.stereotype.Component
import java.io.File
import java.nio.file.Files

@Component
class DataInit(private val trackR: TrackRepository) : CommandLineRunner {
    override fun run(vararg args: String?) {
        if (trackR.count() == 0L) {
            val tracksDir = File("src/main/resources/static/tracks")
            val imagesDir = File("src/main/resources/static/images")
            if (!imagesDir.exists()) imagesDir.mkdirs()
            val tracks = mutableListOf<Track>()

            tracksDir.listFiles { f -> f.extension == "mp3" }?.forEach { file ->
                try {
                    val mp3 = Mp3File(file)
                    var imagePath: String? = null
                    if (mp3.hasId3v2Tag()) {
                        val tag = mp3.id3v2Tag
                        val imageData = tag.albumImage
                        if (imageData != null) {
                            val imageName = file.nameWithoutExtension + ".jpg"
                            val imageFile = File(imagesDir, imageName)
                            Files.write(imageFile.toPath(), imageData)
                            imagePath = "/images/$imageName"
                        }
                    }

                    val title = mp3.id3v2Tag?.title ?: file.nameWithoutExtension
                    val artist = mp3.id3v2Tag?.artist ?: "Unknown Artist"
                    val duration = (mp3.lengthInMilliseconds).toInt()

                    tracks += Track(
                        title = title,
                        artist = artist,
                        url = "http://10.0.2.2:8080/tracks/${file.name}",
                        duration = duration,
                        imageUrl = "http://10.0.2.2:8080$imagePath"
                    )
                    println("Creating track: $title")
                    println("URL: ${tracks.last().url}")
                    println("Image: ${tracks.last().imageUrl}")
                } catch (e: Exception) {
                    println("⚠️ Ошибка при обработке ${file.name}: ${e.message}")
                }
            }
            trackR.saveAll(tracks)
            println("✅ Added ${tracks.size} demo tracks with covers")
        }

    }
}
