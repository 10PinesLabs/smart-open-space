package com.sos.smartopenspace.config

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.kotlinModule
import java.io.IOException
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

object ObjectMapperUtil {

    const val DATE_FORMAT = "yyyy-MM-dd"
    const val TIME_FORMAT = "HH:mm"

    fun build(): ObjectMapper {
        val objectMapperBuilder = JsonMapper.builder().enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)

        objectMapperBuilder.enable(MapperFeature.USE_GETTERS_AS_SETTERS)
        objectMapperBuilder.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        objectMapperBuilder.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        objectMapperBuilder.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)

        objectMapperBuilder.enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE)

        val javaTimeModule = JavaTimeModule()
        javaTimeModule.addSerializer(LocalTime::class.java, localTimeSerializer())
        javaTimeModule.addSerializer(LocalDate::class.java, localDateSerializer())
        objectMapperBuilder.addModule(javaTimeModule)

        objectMapperBuilder.addModule(kotlinModule())

        return objectMapperBuilder.build()
    }

    private fun localTimeSerializer(): JsonSerializer<LocalTime> {
        return object : JsonSerializer<LocalTime>() {
            @Throws(IOException::class)
            override fun serialize(
                localTime: LocalTime, jsonGenerator: JsonGenerator,
                serializerProvider: SerializerProvider
            ) {
                jsonGenerator.writeString(localTime.format(DateTimeFormatter.ofPattern(TIME_FORMAT)))
            }
        }
    }

    private fun localDateSerializer(): JsonSerializer<LocalDate> {
        return object : JsonSerializer<LocalDate>() {
            @Throws(IOException::class)
            override fun serialize(
                localDate: LocalDate, jsonGenerator: JsonGenerator,
                serializerProvider: SerializerProvider
            ) {
                jsonGenerator.writeString(localDate.format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
            }
        }
    }

}