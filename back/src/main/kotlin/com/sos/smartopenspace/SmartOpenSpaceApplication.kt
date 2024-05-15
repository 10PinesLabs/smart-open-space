package com.sos.smartopenspace

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.runApplication

//TODO: Remove SecurityAutoConfiguration::class when we have security configured
@SpringBootApplication( exclude = [ SecurityAutoConfiguration::class ])
class SmartOpenSpaceApplication

fun main(args: Array<String>) {
  runApplication<SmartOpenSpaceApplication>(*args)
}
