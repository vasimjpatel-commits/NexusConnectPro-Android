package com.nexusconnect.pro
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
object AuthTokenStore {
 private const val PREFS="nexus_auth"; private const val ACCESS="access_token"; private const val REFRESH="refresh_token"
 private fun prefs(c:Context)=EncryptedSharedPreferences.create(c,PREFS,MasterKey.Builder(c).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
 fun save(c:Context,a:String,r:String){prefs(c).edit().putString(ACCESS,a).putString(REFRESH,r).apply()}
 fun access(c:Context)=prefs(c).getString(ACCESS,null)
 fun refresh(c:Context)=prefs(c).getString(REFRESH,null)
 fun clear(c:Context){prefs(c).edit().clear().apply()}
}