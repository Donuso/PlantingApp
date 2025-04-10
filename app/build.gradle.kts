plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.plantingapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.plantingapp"
        minSdk = 26
        targetSdk = 34
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
    packagingOptions {
        exclude ("META-INF/DEPENDENCIES")
        exclude ("META-INF/LICENSE.md")
        exclude("META-INF/NOTICE.md")
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
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.16.0")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
    implementation("androidx.core:core-animation:1.0.0")
    implementation("androidx.work:work-runtime-ktx:2.7.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.github.Dimezis:BlurView:version-2.0.6")
    implementation ("com.squareup.okhttp3:okhttp:4.9.1")
    implementation ("com.google.android.gms:play-services-location:21.0.1")
    implementation ("com.wdullaer:materialdatetimepicker:4.0.0")
    implementation ("androidx.recyclerview:recyclerview:1.3.0")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation ("com.davemorrissey.labs:subsampling-scale-image-view-androidx:3.10.0")
    implementation ("com.tencent.map.geolocation:TencentLocationSdk-openplatform:7.5.4.3")
    implementation ("com.github.esafirm:android-image-picker:3.0.0")
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation ("com.aliyun:aliyun-java-sdk-core:4.5.16")
    implementation ("com.aliyun:aliyun-java-sdk-dashscope:1.0.0")
    /*implementation ("group: 'com.alibaba';" + "name: 'dashscope-sdk-java';" + "version: 'the-latest-version'")*/
}