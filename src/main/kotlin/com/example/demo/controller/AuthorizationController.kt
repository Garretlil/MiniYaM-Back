package com.example.demo.controller
import java.util.UUID
import at.favre.lib.crypto.bcrypt.BCrypt
import com.example.demo.UserRepository
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import kotlin.to


data class User(
    val name:String?,
    val email:String,
    val password:String
)

fun generateToken(): String {
    return UUID.randomUUID().toString()
}
fun hashPassword(pass:String):String {
    return BCrypt.withDefaults().hashToString(12, pass.toCharArray())
}


@RestController
class AuthorizationController(
    private val userRepository: UserRepository,
    ) {
    @PostMapping("/register")
    fun register(@RequestBody user: User): ResponseEntity<Map<String, String>> {
        try {
            val existingUser = userRepository.findByEmail(user.email)
            if (existingUser != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(mapOf("message" to "User already exist"))
            }
            else {
                val password = hashPassword(user.password)
                val token = generateToken()
                userRepository.save<com.example.demo.model.entity.User>(
                    com.example.demo.model.entity.User(
                        name = user.name!!,
                        email = user.email,
                        password = password,
                        token = token
                    )
                )
                return ResponseEntity.status(HttpStatus.OK)
                    .body(mapOf("message" to token))
            }
        }
        catch (e: Exception) {
            //e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(mapOf("message" to "Server error"))
        }
    }
    @PostMapping("/login")
    fun login(@RequestBody user: User): ResponseEntity<Map<String, String>> {
        val existingUser = userRepository.findByEmail(user.email)
            ?: return ResponseEntity.status(404).body(mapOf("message" to "User does not exist"))
        val result = BCrypt.verifyer().verify(user.password.toCharArray(), existingUser.password)
        return if (result.verified) {
            ResponseEntity.ok(mapOf("token" to existingUser.token))
        } else {
            ResponseEntity.status(401).body(mapOf("message" to "Wrong password"))
        }
    }
}

