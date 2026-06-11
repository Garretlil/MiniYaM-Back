package com.example.afb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AfbApplication

fun main(args: Array<String>) {
	runApplication<AfbApplication>(*args)
}
