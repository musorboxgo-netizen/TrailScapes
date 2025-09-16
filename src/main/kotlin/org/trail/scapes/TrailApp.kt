package org.trail.scapes

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class TrailApp

fun main(args: Array<String>) {
    SpringApplication.run(TrailApp::class.java, *args)
}

