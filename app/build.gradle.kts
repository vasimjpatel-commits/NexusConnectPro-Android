plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nexusconnect.pro"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.nexusconnect.pro"
        minSdk = 24
        targetSdk = 35
        versionCode = 2
        versionName = "1.1"
        // Replace with your deployed HTTPS API URL before building.
        buildConfigField("String", "AUTH_BASE_URL", "\"https://nexusconnectpro-android-production.up.railway.app\"")
    }
    buildFeatures { buildConfig = true }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
}
