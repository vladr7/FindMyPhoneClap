@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.hilt)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms)
    alias(libs.plugins.firebase.crashlytics)
}

android {
    namespace = "com.riviem.findmyphoneclap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.riviem.findmyphoneclap"
        minSdk = 27
        targetSdk = 34
        versionCode = 3
        versionName = "1.0.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    aaptOptions {
        noCompress += "tflite"
    }
}

dependencies {

    implementation(libs.bundles.compose)
    implementation(libs.gms.auth)
    implementation(libs.bundles.compose)
    implementation(libs.bundles.firebase)
    implementation(libs.bundles.hilt)
    implementation(libs.bundles.androidx)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil)
    implementation(libs.media3)
    implementation(libs.media3Ui)
    implementation(libs.media3Session)
    implementation(libs.kotlinx.serialization)
    implementation(libs.datastore)
    implementation(libs.bundles.retrofitAndSerialization)
    implementation(libs.squareup.okHttp)
    implementation(libs.maps.playServices)
    implementation(libs.maps.compose)
    implementation(libs.playServiceCodeScanner)
    kapt(libs.google.hiltandroidcompiler)
    implementation(libs.lottie)
    implementation(libs.image.compressor)
    implementation(libs.play.review)
    implementation(libs.play.review.ktx)
    implementation(libs.bundles.accompanist)
    implementation(libs.bundles.appupdate)
    implementation(libs.tensorflow.lite.audio)
    implementation("androidx.compose.ui:ui-text-google-fonts:1.4.3")
    implementation("com.google.android.gms:play-services-basement:17.6.0")



    testImplementation(libs.junit)
    testImplementation(libs.androidx.test.junit)
    testImplementation(libs.androidx.test.espresso.core)
    testImplementation(libs.compose.testing)
    testImplementation(libs.bundles.testing)
    debugImplementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    debugImplementation(libs.navigation)
    testCompileOnly(libs.hamcrest)
    testCompileOnly(libs.kotlinx.coroutines.test)
}