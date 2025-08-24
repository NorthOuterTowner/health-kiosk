package com.example.healthkiosk

import android.provider.ContactsContract.CommonDataKinds.Photo
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.*
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import java.io.File

data class LoginRequest(val account: String, val pwd: String)
object ApiService {
    /*Login API*/
    suspend fun login(name: String, pwd: String): String {
        val response: HttpResponse = NetworkClient.httpClient.post("/admin/login") {
            contentType(ContentType.Application.Json)
            //body = Json.encodeToString(LoginRequest(name, pwd))
            val jsonBody = """{"account":"$name","pwd":"$pwd"}"""
            setBody(jsonBody)
            //setBody(LoginRequest(name,pwd))
        }
        return response.bodyAsText()
    }

    /*Register API*/
    suspend fun register(account:String,name: String, pwd: String, photoFile: File): String {
        val response: HttpResponse = NetworkClient.httpClient.post("/admin/register") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("account",account)
                        append("name", name)
                        append("pwd",pwd)
                        append(
                            "photo",
                            photoFile.readBytes(),
                            Headers.build {
                                append(HttpHeaders.ContentType, "image/jpeg")
                                append(HttpHeaders.ContentDisposition, "filename=\"${photoFile.name}\"")
                            }
                        )
                    }
                )
            )
        }
        return response.bodyAsText()
    }


    /*Test API: Test whether the connection is normal*/
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