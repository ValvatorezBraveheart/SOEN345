import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}
val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))
android {
    namespace = "com.example.soen345"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.soen345"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
        buildConfigField("String", "TWILIO_ACCOUNT_SID", "\"${localProperties["TWILIO_ACCOUNT_SID"]}\"")
        buildConfigField("String", "TWILIO_AUTH_TOKEN", "\"${localProperties["TWILIO_AUTH_TOKEN"]}\"")
        buildConfigField("String", "TWILIO_SRC_PHONE", "\"${localProperties["TWILIO_SRC_PHONE"]}\"")
        buildConfigField("String", "GMAIL_EMAIL", "\"${localProperties["GMAIL_EMAIL"]}\"")
        buildConfigField("String", "GMAIL_APP_PASSWORD", "\"${localProperties["GMAIL_APP_PASSWORD"]}\"")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
        unitTests {
            isReturnDefaultValues = true
        }
    }
    packaging {
        resources {
            excludes += setOf(
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/NOTICE.md")
        }
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.twilio)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform(libs.firebase.bom))
    implementation (libs.android.mail)
    implementation(libs.android.activation)

    // Add the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation(libs.firebase.firestore)

    // Other existing dependencies...
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.jbcrypt)

    implementation(libs.material.v1110)
    implementation(libs.cardview)
    implementation(libs.constraintlayout.v214)



    testImplementation(libs.junit.jupiter.api.v5102)
    testImplementation(libs.junit.jupiter.params)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.junit.jupiter)
    testImplementation(libs.robolectric)
}