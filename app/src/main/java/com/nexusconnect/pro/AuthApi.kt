package com.nexusconnect.pro
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
object AuthApi {
 private fun post(path:String,body:JSONObject):JSONObject {
  val c=URL(BuildConfig.AUTH_BASE_URL.trimEnd('/')+path).openConnection() as HttpURLConnection
  c.requestMethod="POST"; c.connectTimeout=15000; c.readTimeout=15000; c.doOutput=true
  c.setRequestProperty("Content-Type","application/json"); c.setRequestProperty("Accept","application/json")
  return try {
   c.outputStream.use{it.write(body.toString().toByteArray(Charsets.UTF_8))}
   val stream=if(c.responseCode in 200..299)c.inputStream else c.errorStream
   val j=JSONObject(stream?.bufferedReader()?.use{it.readText()} ?: "{}")
   if(c.responseCode !in 200..299) throw IllegalStateException(j.optString("error","Authentication failed ("+c.responseCode+")"))
   j
  } finally {c.disconnect()}
 }
 fun register(n:String,e:String,p:String)=post("/v1/auth/register",JSONObject().put("name",n).put("email",e).put("password",p))
 fun login(e:String,p:String)=post("/v1/auth/login",JSONObject().put("email",e).put("password",p))
 fun requestOtp(p:String)=post("/v1/auth/otp/request",JSONObject().put("phone",p))
 fun verifyOtp(p:String,code:String)=post("/v1/auth/otp/verify",JSONObject().put("phone",p).put("code",code))
 fun refresh(t:String)=post("/v1/auth/refresh",JSONObject().put("refreshToken",t))
 fun logout(t:String)=post("/v1/auth/logout",JSONObject().put("refreshToken",t))
}