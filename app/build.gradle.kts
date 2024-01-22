plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("androidx.navigation.safeargs.kotlin")
    kotlin("kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.yousefelsayed.gamescheap"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.yousefelsayed.gamescheap"
        minSdk = 24
        targetSdk = 33
        versionCode = 8
        versionName = "2.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    dataBinding {
        this.enable = true
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    val glideVersion = "4.15.1"
    val shimmerVersion = "0.5.0"
    val lifecycleVersion = "2.7.0"
    val navVersion = "2.7.6"
    val hiltVersion = "2.48.1"
    val retrofitVersion = "2.9.0"
    val okhttpVersion = "4.10.0"
    val scalarsVersion = "2.5.0"

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //Design
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    kapt("com.github.bumptech.glide:compiler:$glideVersion")
    implementation("com.facebook.shimmer:shimmer:$shimmerVersion")
    implementation("androidx.palette:palette-ktx:1.0.0")
    implementation("com.facebook.shimmer:shimmer:0.5.0")
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")
    //ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    //Navigation component implementation
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    //Dependency injection implementation 'Hilt'
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-android-compiler:$hiltVersion")
    //API Calls 'Retrofit'
    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
    implementation("com.squareup.retrofit2:converter-scalars:$scalarsVersion")
}
kapt {
    correctErrorTypes = true
}