package com.example.healthkiosk

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object NetworkClient {
    val httpClient = HttpClient(CIO) {
        // 1. 基础JSON序列化配置
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }

        // 2. 日志配置（开发调试用）
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    println("Ktor Client: $message")
                }
            }
        }

        // 3. 默认请求配置
        defaultRequest {
            url {
                protocol = URLProtocol.HTTP
                host = "10.0.2.2"
                port = 3000
            }
            contentType(ContentType.Application.Json)
        }
    }
}