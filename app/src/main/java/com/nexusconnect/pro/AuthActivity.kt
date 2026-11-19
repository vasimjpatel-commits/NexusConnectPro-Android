package com.nexusconnect.pro
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import java.util.concurrent.Executors
class AuthActivity:Activity(){
 private val worker=Executors.newSingleThreadExecutor()
 private lateinit var status:TextView
 private lateinit var name:EditText
 private lateinit var email:EditText
 private lateinit var password:EditText
 private lateinit var phone:EditText
 private lateinit var code:EditText
 private lateinit var emailPanel:LinearLayout
 private lateinit var phonePanel:LinearLayout
 override fun onCreate(savedInstanceState:Bundle?){super.onCreate(savedInstanceState);window.statusBarColor=Color.rgb(11,15,25); buildUi()
  AuthTokenStore.refresh(this)?.let{t->runAuth("Restoring session…"){val r=AuthApi.refresh(t);saveAndOpen(r)}}
 }
 private fun buildUi(){
  val root=LinearLayout(this).apply{orientation=1;setPadding(24,32,24,24);setBackgroundColor(Color.rgb(11,15,25))}
  val scroll=ScrollView(this);scroll.addView(root);setContentView(scroll)
  root.addView(label(getString(R.string.brand_startup_name),22f,Color.WHITE));root.addView(label(getString(R.string.app_name),26f,Color.WHITE));root.addView(label(getString(R.string.brand_creator_credit),12f,Color.LTGRAY));root.addView(label("Sign in securely to continue",14f,Color.LTGRAY))
  val tabs=LinearLayout(this).apply{orientation=0};tabs.addView(button("Email"){emailPanel.visibility=View.VISIBLE;phonePanel.visibility=View.GONE},LinearLayout.LayoutParams(0,-2,1f));tabs.addView(button("Phone OTP"){emailPanel.visibility=View.GONE;phonePanel.visibility=View.VISIBLE},LinearLayout.LayoutParams(0,-2,1f));root.addView(tabs)
  emailPanel=LinearLayout(this).apply{orientation=1}
  name=field("Full name (new account)");email=field("Email address");email.inputType=33;password=field("Password (10+ characters)");password.inputType=129
  emailPanel.addView(name);emailPanel.addView(email);emailPanel.addView(password)
  emailPanel.addView(button("Sign in"){emailAction(false)});emailPanel.addView(button("Create account"){emailAction(true)});root.addView(emailPanel)
  phonePanel=LinearLayout(this).apply{orientation=1;visibility=View.GONE}
  phone=field("Phone, international format e.g. +919876543210");phone.inputType=3;code=field("SMS verification code");code.inputType=2
  phonePanel.addView(phone);phonePanel.addView(button("Send OTP"){runAuth("Requesting code…"){AuthApi.requestOtp(phone.text.toString().trim());show("OTP requested. Check your SMS.")}})
  phonePanel.addView(code);phonePanel.addView(button("Verify and sign in"){runAuth("Verifying…"){saveAndOpen(AuthApi.verifyOtp(phone.text.toString().trim(),code.text.toString().trim()))}})
  root.addView(phonePanel);status=label("",13f,Color.LTGRAY);root.addView(status)
 }
 private fun emailAction(create:Boolean){val n=name.text.toString().trim();val e=email.text.toString().trim();val p=password.text.toString()
  if(e.isBlank()||p.isBlank()||(create&&n.isBlank())){show("Complete the required fields.");return}
  runAuth(if(create)"Creating account…" else "Signing in…"){saveAndOpen(if(create)AuthApi.register(n,e,p) else AuthApi.login(e,p))}
 }
 private fun saveAndOpen(r:org.json.JSONObject){AuthTokenStore.save(this,r.getString("accessToken"),r.getString("refreshToken"));runOnUiThread{startActivity(Intent(this,MainActivity::class.java));finish()}}
 private fun runAuth(msg:String,task:()->Unit){show(msg);worker.execute{try{task()}catch(e:Exception){show(e.message?:"Network or server error")}}}
 private fun show(s:String){runOnUiThread{status.text=s}}
 private fun label(s:String,size:Float,color:Int)=TextView(this).apply{text=s;textSize=size;setTextColor(color);gravity=1;setPadding(0,12,0,12)}
 private fun field(h:String)=EditText(this).apply{hint=h;setHintTextColor(Color.GRAY);setTextColor(Color.WHITE);setPadding(14,12,14,12);setBackgroundColor(Color.rgb(27,36,53))}
 private fun button(s:String,action:()->Unit)=Button(this).apply{text=s;setTextColor(Color.WHITE);setBackgroundColor(Color.rgb(79,70,229));setOnClickListener{action()}}
 override fun onDestroy(){worker.shutdown();super.onDestroy()}
}