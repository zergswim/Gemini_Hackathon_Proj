# Project Name : GemiQuiz (Gemini Hackathon)

## Skill & Environment
Gemini 1.5 Flash API<br>
Google Image Search API<br>
Android Studio (tested by API Level 26 : Android 8.0(Oreo) )<br>
<br>

## [흐름도 설명]
![images](https://github.com/user-attachments/assets/38a2f4f2-c194-4f9b-afe2-788ec97951fc)
<br>

## Screen 1
![screen1](https://github.com/user-attachments/assets/76d9856f-d75b-44e4-b71a-70a592ec3c7e)

## Screen 2
![screen2](https://github.com/user-attachments/assets/dc2ebd0f-cfe5-473b-93d8-194a4d56fe75)

## Screen 3
![screen3](https://github.com/user-attachments/assets/df64dcff-dc75-41ca-8341-4cd989fc5a58)
<br>

## Youtube 시연
[![image](https://github.com/user-attachments/assets/5f1338c5-9ceb-4c53-b59f-eecab151f0e7)](https://www.youtube.com/watch?v=-V13tHHUYQI)

## [참조 : libs.versions.toml]
<pre>
[versions]
agp = "8.5.0"
kotlin = "1.9.0"
coreKtx = "1.13.1"
junit = "4.13.2"
junitVersion = "1.2.1"
espressoCore = "3.6.1"
lifecycleRuntimeKtx = "2.8.3"
lifecycleViewmodelCompose = "2.8.3"
activityCompose = "1.9.0"
composeBom = "2024.04.01"
generativeai = "0.7.0" #"0.2.2"  #
googleAndroidLibrariesMapsplatformSecretsGradlePlugin = "2.0.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "coreKtx" }
junit = { group = "junit", name = "junit", version.ref = "junit" }
androidx-junit = { group = "androidx.test.ext", name = "junit", version.ref = "junitVersion" }
androidx-espresso-core = { group = "androidx.test.espresso", name = "espresso-core", version.ref = "espressoCore" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleViewmodelCompose" }
androidx-activity-compose = { group = "androidx.activity", name = "activity-compose", version.ref = "activityCompose" }
androidx-compose-bom = { group = "androidx.compose", name = "compose-bom", version.ref = "composeBom" }
androidx-ui = { group = "androidx.compose.ui", name = "ui" }
androidx-ui-graphics = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-ui-tooling = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-material3 = { group = "androidx.compose.material3", name = "material3" }
generativeai = { group = "com.google.ai.client.generativeai", name = "generativeai", version.ref = "generativeai" }

[plugins]
android-application = { id = "com.android.application", version.ref = "agp" }
jetbrains-kotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }
google-android-libraries-mapsplatform-secrets-gradle-plugin = { id = "com.google.android.libraries.mapsplatform.secrets-gradle-plugin", version.ref = "googleAndroidLibrariesMapsplatformSecretsGradlePlugin" }
</pre>

