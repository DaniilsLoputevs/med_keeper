plugins {
    id("com.android.application")
    kotlin("android")
    id("com.google.devtools.ksp") version "1.8.20-1.0.11" //Config for Koin annotations - autogenerated classes
}
val koin_version = "3.3.0"
val koin_ksp_version = "1.1.0"


android {
    namespace = "solutions.mk.mobile"
    compileSdk = 32

    defaultConfig {
        applicationId = "solutions.mk.mobile"
        minSdk = 24
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }


    /* Koin generated sources
    * Android has different way from other platform to declare generated source paths.
    * And it wast hard to google https://discuss.kotlinlang.org/t/kotlin-gradle-script-add-generated-kotlin-sources/17277 */
    kotlin {
        sourceSets.main {
            kotlin {
                // default "main" & "test" source paths declared by default - IDK where
                srcDirs("build/generated/ksp/main/kotlin")
                srcDirs("build/generated/ksp/test/kotlin")
            }
        }
    }
}



dependencies {
    /* IMPORTANT!!! - update version of dependency -> require up  SDK version -> fewer devices may use this app
    * This is the latest version that we can use for (compileSdk = 32) */
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    installKoin()
}

fun DependencyHandlerScope.installKoin() {
    implementation("io.insert-koin:koin-android:$koin_version")
    implementation("io.insert-koin:koin-annotations:$koin_ksp_version")
    ksp("io.insert-koin:koin-ksp-compiler:$koin_ksp_version")
    //    implementation("io.insert-koin:koin-logger-slf4j:$koin_version") //To use slf4logger in install block in App module for koin

}