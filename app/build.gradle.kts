plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.arifin.newest"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.arifin.newest"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs += ("-Xopt-in=kotlin.RequiresOptIn")
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation ("androidx.core:core-ktx:1.10.1")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation ("com.google.android.gms:play-services-maps:18.1.0")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("androidx.datastore:datastore-preferences:1.0.0")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")

// retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

// gson converter
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

// logging interceptor
    implementation ("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.11")

    implementation ("com.github.bumptech.glide:glide:4.15.1")

// activity KTX
    implementation ("androidx.activity:activity-ktx:1.7.1")

// fragment KTX
    implementation ("androidx.fragment:fragment-ktx:1.5.7")

// camera
    implementation ("androidx.camera:camera-camera2:1.2.2")
    implementation ("androidx.camera:camera-lifecycle:1.2.2")
    implementation ("androidx.camera:camera-view:1.2.2")

    implementation ("androidx.room:room-ktx:2.5.2")
    implementation ("androidx.room:room-common:2.5.1")
    implementation ("androidx.room:room-paging:2.5.1")
    implementation ("androidx.paging:paging-runtime-ktx:3.1.1")
    kapt ("androidx.room:room-compiler:2.5.1")

    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0") //InstantTaskExecutorRule
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4") //TestDispatcher
    testImplementation ("androidx.arch.core:core-testing:2.2.0") // InstantTaskExecutorRule
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4") //TestDispatcher

//mockito
    testImplementation ("org.mockito:mockito-core:4.4.0")
    testImplementation ("org.mockito:mockito-inline:4.4.0")
}