plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.munimji"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.munimji"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Signing config will be added if keystore is present at app/keystore/release-keystore.jks
            signingConfig = signingConfigs.findByName("release")
        }
    }

    signingConfigs {
        // A simple local signing config. If you prefer to use your own keystore, replace these values
        create("release") {
            storeFile = file("keystore/release-keystore.jks")
            storePassword = "munimji123"
            keyAlias = "munimji"
            keyPassword = "munimji123"
        }
    }

    lint {
        disable.addAll(listOf("MissingDimensionAndroidResource", "InvalidPackage", "PropertyEscape"))
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // Image loading & Work
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.5") {
        exclude(group = "androidx.navigationevent", module = "navigationevent-android")
    }

    // Firebase (use explicit KTX versions to avoid BOM parsing issues in this environment)
    implementation("com.google.firebase:firebase-auth-ktx:22.1.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.5.0")
    implementation("com.google.firebase:firebase-messaging-ktx:23.2.0")

    // Room
    implementation("androidx.room:room-ktx:2.5.2")
    kapt("androidx.room:room-compiler:2.5.2")

    // Barcode Generation (ZXing)
    implementation("com.google.zxing:core:3.5.3")

    // CSV Support (lightweight, no API 26 requirements)
    implementation("com.opencsv:opencsv:5.8")

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
