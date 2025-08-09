package com.example.healthkiosk

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

data class LoginRequest(val name: String, val pwd: String)

object ApiService {
    suspend fun login(name: String, pwd: String): String {
        val response: HttpResponse = NetworkClient.httpClient.post("/login") {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(name, pwd))
        }
        return response.bodyAsText()
    }

    suspend fun test():String {
        val response: HttpResponse = NetworkClient.httpClient.get("http://10.0.2.2:3000/health")
        return try {
            val response = NetworkClient.httpClient.get("/health")
            response.bodyAsText()
        } catch (e: ClientRequestException) {
            "客户端错误: ${e.response.status}"  // 4xx
        } catch (e: ServerResponseException) {
            "服务端错误: ${e.response.status}"  // 5xx
        } catch (e: HttpRequestTimeoutException) {
            "请求超时"  // 408
        } catch (e: Exception) {
            "网络错误: ${e.message}"  // 其他错误
        }
    }

}