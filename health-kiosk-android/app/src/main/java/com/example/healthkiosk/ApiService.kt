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
  suspend fun test(token: String):String {
    val response: HttpResponse = NetworkClient.httpClient.get("/func/test"){
        headers{
            append("Authorization","Bearer $token")
        }
    }

    return response.bodyAsText()
  }

}